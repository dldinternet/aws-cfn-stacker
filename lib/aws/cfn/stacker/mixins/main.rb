require 'colorize'
require 'inifile'
require 'awesome_print'

module Aws
  module Cfn
    module Stacker
      # noinspection ALL
      module Main

        # --------------------------------------------------------------------------------------------------------------
        MY_NAME = "cfn-stacker"
        NO_COMMAND_GIVEN = "You need to pass a sub-command (e.g., #{MY_NAME} SUB-COMMAND or #{MY_NAME} -a|--action SUB-COMMAND)\n"

        # --------------------------------------------------------------------------------------------------------------
        attr_accessor :logger
        attr_accessor :verbosity
        attr_accessor :LOGLEVELS
        attr_accessor :ALLACTIONS
        # --------------------------------------------------------------------------------------------------------------

        # --------------------------------------------------------------------------------------------------------------
        module ClassMethods

          def loglevels
            @LOGLEVELS
          end

          def loglevels=(levels)
            @LOGLEVELS  = levels || [:trace, :debug, :step, :info, :warn, :error, :fatal, :todo]
          end

          def allactions
            @ALLACTIONS
          end

          def allactions=(acts)
            @ALLACTIONS = acts || [ :build, :configure, :create, :status, :update, :delete, :outputs, :watch, :listparams ]
          end


        end

        # --------------------------------------------------------------------------------------------------------------
        def self.included(includer)
          includer.extend(ClassMethods)
          includer.class_eval do
            self.loglevels  = [:trace, :debug, :step, :info, :warn, :error, :fatal, :todo]
            self.allactions = [ :build, :configure, :create, :status, :update, :delete, :outputs, :watch, :listparams ]
          end
        end

        # --------------------------------------------------------------------------------
        # Create a new instance of the current class configured for the given
        # arguments and options
        def initialize
          $STKR           = self
          @TODO           = {}
          @defaultOptions = {}

          super
        end

        # -----------------------------------------------------------------------------
        def run(argv)
          begin
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
            puts "#{__FILE__}::#{__LINE__} reraising ... "
            raise e
            exit -1
          end
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

        # Reconfigure the application. You'll want to override and super this method.
        def reconfigure
          configure_application
          configure_logging
        end

        # Parse configuration (options and config file)
        def configure_application
          parse_options(ARGV)
          load_config_file
        end

        # --------------------------------------------------------------------------------

        # Parse the config file
        def load_config_file(path=nil)
          <<-EOC
          Loads config files from a given path, or additional paths if not specified.

          If a specific path isn't specified, loads the following locations:
            - $CWD/config/config.ini
            - $HOME/.stacker/config.ini
            - /etc/stacker/config.ini
            - /usr/local/etc/stacker/config.ini

          Establishes default values for a handful of section variables:
            - disable_rollback = 'true' - don't automatically rollback on error
            - s3_bucket = 'amplify-sto-templates' - s3 bucket to upload templates to
              if needed.
            - cf_template_dir = $CWD/templates - location of CloudFormation templates
            - stack_dir = $CWD/stacks - location where stack config directories are stored
            - playbooks_dir = $CWD/ansible/playbooks
          EOC
          cwd = Dir.getwd()

          defaults={
              disable_rollback: true,
              template_dir:   File.join(cwd, 'templates'),
              stack_dir:      File.join(File.dirname(@options[:config_file]), 'stacks'),
              playbooks_dir:  File.join(cwd, 'ansible', 'playbooks')
          }
          #config = ConfigParser.ConfigParser(

          paths = [
              File.join(cwd,'config', 'config.ini'),
              File.join(File.expand_path("~"), 'config.ini'),
              '/etc/stacker/config.ini',
              '/usr/local/etc/stacker/config.ini',
          ]

          if path
            paths.unshift path
          end
          config = nil
          paths.each do |path|
            begin
              config = IniFile.load(path)
              @inis << path
              break
            rescue => e
              # noop
            end
          end
          return config
        end

        def configure_logging
          super
          @config[:log_opts] = lambda{|mlll| {
              :pattern      => "%#{mlll}l: %m %C\n",
              :date_pattern => '%Y-%m-%d %H:%M:%S',
          }
          }

          @logger = getLogger(@config)
        end

        def configure_stdout_logger
        end

        # Called prior to starting the application, by the run method
        def setup_application
          raise ApplicationError, "#{self.to_s}: you must override setup_application"
        end

        # Actually run the application
        def run_application
          raise ApplicationError, "#{self.to_s}: you must override run_application"
        end


      end
    end
  end
end
