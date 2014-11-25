module Aws
  module Cfn
    module Stacker
      # noinspection ALL
      module Main

        # --------------------------------------------------------------------------------------------------------------
        @MY_NAME = "cfn-stacker"
        @NO_COMMAND_GIVEN = "You need to pass a sub-command (e.g., #{@MY_NAME} SUB-COMMAND or #{@MY_NAME} -a|--action SUB-COMMAND)\n"

        # --------------------------------------------------------------------------------------------------------------
        attr_accessor :actors
        # attr_accessor :ALLACTIONS
        # --------------------------------------------------------------------------------------------------------------
        # ALLACTIONS = [ :build, :configure, :create, :status, :update, :delete, :outputs, :watch, :listparams, :liststacks ]#, :compare_build_py ]

        # --------------------------------------------------------------------------------------------------------------
        module ClassMethods

          def actors
            @ACTORS
          end

          def actors=(v)
            @ACTORS  = v
          end

          # def allactions
          #   @ALLACTIONS
          # end
          #
          # def allactions=(acts)
          #   @ALLACTIONS = acts || ALLACTIONS
          # end

        end

        # --------------------------------------------------------------------------------------------------------------
        def self.included(includer)
          includer.extend(ClassMethods)
          includer.class_eval do
            requires do
              require 'colorize'
              require 'awesome_print'
            end

            self.actors     = {}
            self.loglevels  = LOGLEVELS
            # self.allactions = ALLACTIONS
          end
        end

        # --------------------------------------------------------------------------------
        # Create a new instance of the current class configured for the given
        # arguments and options
        def initialize()
          $STKR           = self
          @TODO           = {}
          @defaultOptions = {}
          @inis           = []
          super
        end

        # -----------------------------------------------------------------------------
        def run(argv)
          begin
            StackerApplication.load_commands
            @argv           = argv
            prescreen_options()
            quiet_traps()
            reconfigure()
            setup_application()
            run_application()
            # reportTODO(@args)
            exit 0
          rescue StackerError => e
            puts e.message.light_red
            puts "#{__FILE__}:#{__LINE__} reraising ... "
            raise e
            exit -1
          end
        end

        # --------------------------------------------------------------------------------
        #
        # Override Mixlib::CLI.parse_options and
        # knowingly add extra paramater to track argument source
        #
        # We do this by monkey patching this override method at include time instead of statically declaring it
        # in which case the Mixlib::CLI.parse_options seems to "win" most of the time OR the interpreter complains.
        #
        def parse_options(args,source=nil)
          # noinspection RubySuperCallWithoutSuperclassInspection
          cmdargv = super(args)

          prescreen_options(cmdargv)

          @config = parse_and_validate_options(@config,source ? source : "ARGV - #{File.basename __FILE__}::#{__LINE__}")

          unless cmdargv.size > 0
            cmdargv << 'create'
          end
          subcommand_class = StackerApplication.subcommand_class_from(cmdargv, self)
          cmdargv.shift(subcommand_class_args.size)
          argv.shift(subcommand_class_args.size)
          if subcommand_class
            subcommand_class.load_deps
            instance = subcommand_class.new()
            StackerApplication.actors[subcommand_class] = instance
            instance.reconfigure
          end
          @config
        end

        # --------------------------------------------------------------------------------------------------------------
        #
        # Do a quick prescreening of the arguments and bail early for some obvious cases
        #
        def prescreen_options(argv=ARGV)
          # Checking ARGV validity *before* parse_options because parse_options
          # mangles ARGV in some situations
          if no_command_given?
            print_help_and_exit(1, @NO_COMMAND_GIVEN)
          elsif no_subcommand_given?
            if want_help? || want_version?
              print_help_and_exit
            else
              print_help_and_exit(2, @NO_COMMAND_GIVEN)
            end
          end
        end

        # --------------------------------------------------------------------------------------------------------------
        # private
        # --------------------------------------------------------------------------------------------------------------
        def no_subcommand_given?(argv=ARGV)
          argv[0] =~ /^--?[^a]/
        end

        def no_command_given?(argv=ARGV)
          argv.empty?
        end

        def want_help?
          ARGV[0] =~ /^(--help|-h)$/
        end

        def want_version?
          ARGV[0] =~ /^(--version|-v)$/
        end

        def print_help_and_exit(exitcode=1, fatal_message=nil)
          puts "FATAL: #{fatal_message}" if fatal_message

          puts self.opt_parser
          puts
          exit exitcode
        end

        # --------------------------------------------------------------------------------------------------------------
        # private
        # --------------------------------------------------------------------------------------------------------------

        # --------------------------------------------------------------------------------------------------------------
        def quiet_traps
          trap("TERM") do
            exit 1
          end

          trap("INT") do
            exit 2
          end
        end

        <<-EOC
          Establishes default values for a handful of section variables:
            - disable_rollback = 'true' - don't automatically rollback on error
            - s3_bucket = 'amplify-sto-templates' - s3 bucket to upload templates to if needed.
            - cf_template_dir = $CWD/templates - location of CloudFormation templates
            - stack_dir = $CWD/stacks - location where stack config directories are stored
            - playbooks_dir = $CWD/ansible/playbooks
        EOC

        def load_config_defaults(path=nil)
          cwd = Dir.getwd()
          defaults={
              disable_rollback: true,
              template_dir:     File.join(cwd, 'templates'),
              stack_dir:        File.join(File.dirname(@config[:config_file]), 'stacks'),
              playbooks_dir:    File.join(cwd, 'ansible', 'playbooks')
          }
        end

      end
    end
  end
end
