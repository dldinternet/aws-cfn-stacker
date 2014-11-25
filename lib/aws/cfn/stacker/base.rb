require 'aws/cfn/stacker/version'
require 'aws/cfn/stacker/subcommand_loader'
require 'aws/cfn/stacker/mixins/convert_to_class_name'
require 'mixlib/cli'

module Aws
  module Cfn
    module Stacker
      LOGLEVELS  = [ :trace, :debug, :info, :step, :warn, :error, :fatal, :todo ]

      class StackerBase

        include ::Mixlib::CLI

        extend Aws::Cfn::Stacker::ConvertToClassName

        # --------------------------------------------------------------------------------------------------------------
        attr_accessor :argv
        attr_accessor :logger
        attr_accessor :LOGLEVELS
        attr_accessor :subcommand_class_name
        attr_accessor :subcommand_class_args

        def argv
          @argv || ARGV
        end

        def argv=(args)
          @argv = args
        end

        # ClassMethods
        # noinspection RubyInstanceVariableNamingConvention
        class << self
          include ::Mixlib::CLI::ClassMethods

          def loglevels
            @LOGLEVELS
          end

          def loglevels=(levels)
            @LOGLEVELS  = levels || LOGLEVELS
          end

          # noinspection RubyClassVariableUsageInspection
          def self.reset_config_path!
            @@stacker_config_dir = nil
          end

          reset_config_path!
          # search upward from current_dir until .chef directory is found
          # noinspection RubyClassVariableUsageInspection
          def stacker_config_dir
            if @@stacker_config_dir.nil? # share this with subclasses
              @@stacker_config_dir = false
              full_path = Dir.pwd.split(File::SEPARATOR)
              (full_path.length - 1).downto(0) do |i|
                candidate_directory = File.join(full_path[0..i] + ["config" ])
                if File.exist?(candidate_directory) && File.directory?(candidate_directory)
                  @@stacker_config_dir = candidate_directory
                  break
                end
              end
            end
            @@stacker_config_dir
          end
          
          def reset_subcommands!
            @subcommands = {}
            @subcommands_by_category = nil
          end

          def inherited(subclass)
            unless subclass.unnamed?
              subcommands[subclass.snake_case_name] = subclass
            end
          end

          # Explicitly set the category for the current command to +new_category+
          # The category is normally determined from the first word of the command
          # name, but some commands make more sense using two or more words
          # ===Arguments
          # new_category::: A String to set the category to (see examples)
          # ===Examples:
          # Data bag commands would be in the 'data' category by default. To put them
          # in the 'data bag' category:
          #   category('data bag')
          def category(new_category)
            @category = new_category
          end

          def subcommand_category
            @category || snake_case_name.split('_').first unless unnamed?
          end

          def snake_case_name
            convert_to_snake_case(name.split('::').last) unless unnamed?
          end

          def common_name
            snake_case_name.split('_').join(' ')
          end

          # Does this class have a name? (Classes created via Class.new don't)
          def unnamed?
            name.nil? || name.empty?
          end

          def subcommand_loader
            @subcommand_loader ||= SubcommandLoader.new(stacker_config_dir)
          end

          def load_commands
            @commands_loaded ||= subcommand_loader.load_commands
          end

          # noinspection RubyClassVariableUsageInspection
          def subcommands
            @@subcommands ||= {}
          end

          def subcommands_by_category
            unless @subcommands_by_category
              @subcommands_by_category = Hash.new { |hash, key| hash[key] = [] }
              subcommands.each do |snake_cased, klass|
                @subcommands_by_category[klass.subcommand_category] << snake_cased
              end
            end
            @subcommands_by_category
          end

          # Print the list of subcommands we know about. If +preferred_category+
          # is given, only subcommands in that category are shown
          def list_commands(preferred_category=nil)
            load_commands

            category_desc = preferred_category ? preferred_category + " " : ''
            puts "Available #{category_desc}subcommands: (for details, #{@stacker_cmd} SUB-COMMAND --help)\n\n"

            if preferred_category && subcommands_by_category.key?(preferred_category)
              commands_to_show = {preferred_category => subcommands_by_category[preferred_category]}
            else
              commands_to_show = subcommands_by_category
            end

            commands_to_show.sort.each do |category, commands|
              next if category =~ /deprecated/i
              puts "** #{category.upcase} COMMANDS **"
              commands.sort.each do |command|
                puts subcommands[command].banner if subcommands[command]
              end
              puts
            end
          end

          def dependency_loaders
            @dependency_loaders ||= []
          end

          def requires(&block)
            dependency_loaders << block
          end

          def load_deps
            dependency_loaders.each do |dep_loader|
              dep_loader.call
            end
          end

          def subcommand_class_from(args, object=nil)
            command_words = args.select {|arg| arg =~ /^(([[:alnum:]])[[:alnum:]\_\-]+)$/ }

            subcommand_class = nil

            while ( !subcommand_class ) && ( !command_words.empty? )
              snake_case_class_name = command_words.join("_").gsub('-', '_')
              subcommand_class = subcommands[snake_case_class_name]
              if subcommand_class
                if object
                  object.subcommand_class_name = snake_case_class_name
                  object.subcommand_class_args = command_words
                end
              else
                command_words.pop
              end
            end
            # see if we got the command as e.g., knife node-list
            subcommand_class ||= subcommands[args.first.gsub('-', '_')]
            subcommand_class || subcommand_not_found!(args)
          end

          # :nodoc:
          # Error out and print usage. probably becuase the arguments given by the
          # user could not be resolved to a subcommand.
          def subcommand_not_found!(args)
            puts "Cannot find sub command for: '#{args.join(' ')}'"

            category_commands = guess_category(args)
            if category_commands
              list_commands(category_commands)
            else
              list_commands
            end

            exit 10
          end

          def guess_category(args)
            category_words = args.select {|arg| arg =~ /^(([[:alnum:]])[[:alnum:]\_\-]+)$/ }
            category_words.map! {|w| w.split('-')}.flatten!
            matching_category = nil
            while (!matching_category) && (!category_words.empty?)
              candidate_category = category_words.join(' ')
              matching_category = candidate_category if subcommands_by_category.key?(candidate_category)
              matching_category || category_words.pop
            end
            matching_category
          end

          attr :stacker_cmd
          def cmd=(cmd)
            @stacker_cmd = cmd
          end

          def cmd
            @stacker_cmd
          end

        end # ClassMethods

        %w(Logging).each { |m|
          require "aws/cfn/stacker/mixins/#{m.downcase}"
          eval "include Aws::Cfn::Stacker::#{m}"
        }

        # -----------------------------------------------------------------------------
        def logError(msg,cat='Error')
          logger = getLogger(@logger_args, 'logError')
          if logger
            if logger.get_trace
              ::Logging::LogEvent.caller_index += 1
            end
            logger.error "#{cat} #{msg} ..."
            if logger.get_trace
              ::Logging::LogEvent.caller_index -= 1
            end
          else
            puts "#{cat} #{msg} ..."
          end
        end

        # Reconfigure the application. You'll want to override and super this method.
        def reconfigure
          configure_application
          configure_alt_options
          configure_logging
        end

        # Parse configuration (options and config file)
        def configure_application
          parse_options(argv)
          load_config_file
        end

        # --------------------------------------------------------------------------------
        def configure_alt_options()
          @config.each do |opt,val|
            if opt.to_s.match(%r'_alt$')
              reg = opt.to_s.gsub(%r'_alt$', '').to_sym
              unless @config[reg] and (@options[reg][:default] != @config[reg])
                @config[reg] = val
              end
            end
          end
        end

        # --------------------------------------------------------------------------------
        def load_config_defaults(path=nil)
          logError "#{self.class.to_s}: you must override #{__method__}"
          {}
        end

        # Parse the config file
        def load_config_file(path=nil)
          <<-EOC
          Loads config files from a given path, or additional paths if not specified.

          If a specific path isn't specified, loads the following locations:
            - $CWD/config/config.ini
            - $HOME/.stacker/config.ini
            - /etc/stacker/config.ini
            - /usr/local/etc/stacker/config.ini

          EOC
          defaults = load_config_defaults(path)

          paths = [
              File.join(Dir.getwd(),'config', 'config.yaml'),
              File.join(File.expand_path("~"), 'config.yaml'),
              '/etc/stacker/config.yaml',
              '/usr/local/etc/stacker/config.yaml',

              File.join(Dir.getwd(),'config', 'config.ini'),
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
              config = if path.match(%r'\.ini$')
                         require 'inifile'
                         IniFile.load(path)
                       else
                         require 'yaml'
                         YAML.load(IO.read(path),path)
                       end
              @inis << path
              break
            rescue => e
              # noop
            end
          end
          config
        end

        def configure_logging
          @config[:log_opts] = lambda{  |mlll|
                                        {
                                          :pattern      => "%#{mlll}l: %m %C\n",
                                          :date_pattern => '%Y-%m-%d %H:%M:%S',
                                        }
                                      }
          @config[:log_levels] ||= LOGLEVELS
          @logger = getLogger(@config)
        end

        def configure_stdout_logger
        end

        # Called prior to starting the application, by the run method
        def setup_application
          logError "#{self.class.to_s}: you must override #{__method__}"
        end

        # Actually run the application
        def run_application
          logError "#{self.class.to_s}: you must override #{__method__}"
        end

      end
    end
  end
end
