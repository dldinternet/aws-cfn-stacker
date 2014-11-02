require 'awesome_print'
require 'inifile'
require 'colorize'

module Aws
  module Cfn
    module Stacker
      module Options

        require 'dldinternet/mixlib/cli/mixins/parsers'
        include ::DLDInternet::Mixlib::CLI::Parsers

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
          argv = super(args)

          prescreen_options(argv)

          @config = parse_and_validate_options(@config,source ? source : "ARGV - #{File.basename __FILE__}::#{__LINE__}")

          if argv.size > 0
            argv.each { |arg|
              action = parseOptionString(arg, ',', 'parseActionSymbol')
              if action
                @config[:actions] << action
              end
            }
          end
          actions = Hash[@config[:actions].flatten.map{ |v| [v, 1] }]
          @config[:actions] = actions.keys
          @config[:actions].each {|action|
            unless StackerApplication.actors[action]
              subcommand_class = StackerApplication.subcommand_class_from([action.to_s])
              subcommand_class.load_deps
              instance = subcommand_class.new()
              StackerApplication.actors[action] = instance
              instance.configure_application
            end
          }
          argv

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

        # --------------------------------------------------------------------------------
        def parseActionSymbol(v)
          if v.to_sym == :all
            StackerApplication.allactions
          else
            s = v.gsub('-', '_').to_sym
            allactions = [StackerApplication.allactions, :all].flatten
            unless allactions.include?(s)
              allactions.each{ |p|
                s = p if p.match(%r/^#{s}/)
              }
            end
            s = StackerApplication.allactions if s == :all
            s
          end
        end

        # --------------------------------------------------------------------------------
        def parsePrecedence(v)
          @prec_max += 1
          match = v.match(%r/^(json|rb|yaml)$/i)
          unless match
            m = "ERROR: Invalid precedence argument: #{v}. Accept only from this set: [json,rb,yaml]"
            puts m
            raise Exception.new(m)
          end
          s = { v => @prec_max }
          match = v.match(%r/^(\S+):(\d+)$/)
          if match
            begin
              a = match[1]
              i = match[2].to_i
              s = { a => i }
            rescue => e
              puts "ERROR: Unable to match precedence #{v}"
              raise e
            end
          end
          s
        end

        # --------------------------------------------------------------------------------
        def parseINIFile(options=nil)
          options = @config unless options
          if options.key?(:inifile)
            logStep "Parse INI file - #{options[:inifile]}"
            raise StackerError.new("Cannot find inifile (#{options[:inifile]})") unless File.exist?(options[:inifile])
            raise StackerError.new("Recursive call to inifile == '#{options[:inifile]}'") if @inis.include?(options[:inifile])
            ini = nil
            begin
              # puts "IniFile.load('#{options[:inifile]}')"
              ini = IniFile.load(options[:inifile])
              @inis << options[:inifile]
              ini['global'].each { |key, value|
                # puts "#{key}=#{value}"
                ENV[key]=value
              }
              argv=[]
              cli = ini['cli'] || []
              cli.each{ |key,value|
                argv << key.gsub(%r/:[0-9]+$/, '').gsub(%r/^([^-])/, '--\1')
                argv << value
              }
              if argv.size > 0
                parse_options(argv,"INI-#{options[:inifile]}")
              end
            rescue => e
              puts e.message.light_red
              raise e
            end
          end
          options
        end

        # -----------------------------------------------------------------------------
        def setDefaultOptions(options)
          @options.each{|name,args|
            if args[:default]
              options[name] = args[:default] unless options[name]
            end
          }
          setOrigins(options,'default')
        end

        # -----------------------------------------------------------------------------
        def validate_options(options=nil)
          options = @config unless options

          # Check for the necessary environment variables
          logStep ('Check ENVironment')
          env = ENV.to_hash
          missing = {}
          %w(AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY).each { |k|
            missing[k] = true unless ENV.has_key?(k)
          }
          if options[:use_chef]
            %w(KNIFE_CHEF_SERVER_URL KNIFE_CLIENT_KEY KNIFE_CLIENT_NAME).each { |k|
              missing[k] = true unless ENV.has_key?(k)
            }
          end

          if missing.count() > 0
            #@logger.error "Missing keys: #{missing.keys.ai}"
            raise StackerError.new("Missing environment variables: #{missing.keys}")
          end
        end

        # -----------------------------------------------------------------------------
        def parse_and_validate_options(options=nil,source='ARGV')
          options = @config unless options
          setOrigins(options,source)

          #options = parseOptions(options,source)
          unless @origins and @name_key_map
            # These are the essential default options which things like parseOptions depend on
            {
                :verbosity    => @verbosity,
                :auto_purge   => false,
            }.each{ |k,v|
              options[k] = v unless options[k]
            }
            setOrigins(options,'hardcoded-default')

            @name_key_map    = {} unless @name_key_map
            @options.each{ |name,args|
              @name_key_map[name]  = {} unless @name_key_map[name]
              [:short,:long,:description].each{|key|
                @name_key_map[name][key] = args[key] if args[key]
              }
            }
          end

          begin
            parseINIFile(options)
            setDefaultOptions(options)
            # Check for all the necessary options
            validate_options(options)
            checkArgsSources(options)
              #findRootPath(options)
          rescue StackerError => e
            puts e.message.light_red
            puts "#{__FILE__}:#{__LINE__} reraising ... "
            raise e
            exit -1
          rescue Exception => e
            puts e.message.light_red
            puts "#{__FILE__}:#{__LINE__} reraising ... "
            raise e
            exit -2
          end

          options
        end

        # ---------------------------------------------------------------------------------------------------------------
        def setOrigins(options,source)
          @origins = {} unless @origins
          options.each { |key, val|
            @origins[key] = source unless (@origins[key])
          }
        end

        # ---------------------------------------------------------------------------------------------------------------
        def checkArgsSources(options)
          if @origins
            missing = @origins.select{ |k,v|
              v.nil?
            }.map{ |k,v| k }
            raise StackerError.new("Missing origins: #{missing.ai}") if missing.size > 0
          end
        end

        module ClassMethods

          # noinspection RubyInstanceVariableNamingConvention
          def defaultoptions=(options)
            @DEFAULTOPTIONS = options || {}
          end

          def defaultoptions
            @DEFAULTOPTIONS
          end

        end

        # --------------------------------------------------------------------------------------------------------------
        #
        # If this module is included we inject this payload into the including class.
        #
        def self.extended(includer)
          included includer
        end
        def self.included(includer)
          includer.extend(::DLDInternet::Mixlib::CLI::Parsers::ClassMethods)
          includer.extend(ClassMethods)

          includer.class_eval do
            self.defaultoptions = {}

            <<-EOF

            -c CONFIG_FILE, --config-file CONFIG_FILE
                                  An ini file to use that contains one section per stack, with all the parameters for the stack enumerated
            -v, --verbose         Increase verbosity, can be specified multiple times (currently just sets the debug level for AWS)
            --debug               Increase debug, can be specified multiple times
            -r, --remove          Delete the requested stack. WARNING: No second chances!
            -d, --delete          Delete the requested stack. WARNING: No second chances!
            -l, --list-params     List the parameters in the template, and show what values are supplied by your config file
            -t TEMPLATE, --template TEMPLATE
                                  Specify a different template to run. Note that specific outputs are expected, so results may vary.
            --template_url TEMPLATE_URL
                                  Specify the key of a template stored in an S3 bucket to run. This method assumes the template has already been uploaded.
            --use_s3              Use an S3 bucket to upload the template to before attempting stack run. This method assumes the template needs to be uploaded and may overwrite a key with the same name.
            -k KEYFILE, --keyfile KEYFILE
                                  The path to the SSH key file to use for SSH to hosts
            -b, --build           Build configuration directory for use with Ansible.
            -i INITIAL_SETUP, --initial_setup INITIAL_SETUP
                                  An initial setup file for Ansible
            -o OUTPUT, --output OUTPUT
                                  Output folder for the Ansible build configuration
            -R ROLES_PATH, --roles_path ROLES_PATH
                                  Folder for the Ansible roles
            --ssh_user SSH_USER   SSH User name
            --ssh_user_bastion SSH_USER_BASTION
                                  Bastion SSH User name
            -u, --update          Update the configuration of an existing stack
            -C, --configure       Run pre-defined ansible playbooks against the given stack. Implies -b
            -s, --status          Print the current status of a given stack.
            -p, --progress        Print a progress indicator during stack create/update/delete.

            EOF

            option  :help,
                    short:        '-h',
                    long:         '--help',
                    description:  'Show this message',
                    show_options: true,
                    exit:         1
            # print the version.
            option  :version,
                    short:        '-V',
                    long:         '--version',
                    description:  'Show version',
                    proc:         Proc.new{ puts ::Aws::Cfn::Stacker::VERSION },
                    exit:         2
            option  :config_file_alt,
                    short:        '-c',
                    long:         '--config-file FILE',
                    description:  'A config file to use that contains one section per stack, with all the parameters for the stack enumerated. INI, YAML and JSON formats supported.',
                    proc:         lambda{ |v|
                      @options[:config_file] = v
                    }
            option  :config_file,
                    short:        '-c',
                    long:         '--config_file FILE',
                    description:  'A config file to use that contains one section per stack, with all the parameters for the stack enumerated. INI, YAML and JSON formats supported.',
                    default:      'config/config.ini'
            option  :verbose,
                    short:        '-v',
                    long:         '--verbose',
                    description:  'Increase verbosity, can be specified multiple times',
                    proc:         lambda {|v|
                      index = $STKR.class.loglevels.index(@options[:log_level]) || $STKR.class.loglevels.index(:warn)
                      index -= 1 if index > 0
                      @options[:log_level] = $STKR.class..loglevels[index]
                    }
            option  :log_level,
                    long:         '--debug',
                    description:  'Set debug level logging. No effect if specified second time.',
                    default:      :debug
            option  :actions,
                    short:        '-a',
                    long:         '--action ACTION',
                    description:  "Perform the requested action against the stack. (#{StackerApplication.allactions.to_s})",
                    proc:         lambda{|v|
                      actions = $STKR.parseOptionString(v,',', 'parseActionSymbol')
                      all     = [StackerApplication.allactions, :all].flatten
                      actions.each{ |act|
                        unless all.include?(act.to_sym)
                          raise ::OptionParser::InvalidOption.new("Invalid action: #{act.to_s}. Valid actions are: #{all.to_s}")
                        end
                      }
                      actions
                    },
                    default:      [ :create ]
            option  :build,
                    short:        '-b',
                    long:         '--build',
                    description:  'Build configuration directory for use with Ansible.',
                    proc:         lambda { |v| @options[:action] = :build}
            option  :remove,
                    short:        '-r',
                    long:         '--remove',
                    description:  'Delete the requested stack. WARNING: No second chances!',
                    proc:         lambda { |v| @options[:action] = :delete }
            option  :delete,
                    short:        '-d',
                    long:         '--delete',
                    description:  'Delete the requested stack. WARNING: No second chances!',
                    proc:         lambda { |v| @options[:action] = :delete }
            option  :update,
                    short:        '-u',
                    long:         '--update',
                    description:  'Update the configuration of an existing stack',
                    proc:         lambda { |v| @options[:action] = :update}
            option  :listparams,
                    short:        '-l',
                    long:         '--list-params',
                    description:  'List the parameters in the template, and show what values are supplied by your config file',
                    proc:         lambda { |v| @options[:action] = :listparams}
            option  :configure,
                    short:        '-C',
                    long:         '--configure',
                    description:  'Run pre-defined ansible playbooks against the given stack. Implies -b',
                    proc:         lambda { |v| @options[:action] = :configure}
            option  :status,
                    short:        '-s',
                    long:         '--status',
                    description:  'Run pre-defined ansible playbooks against the given stack. Implies -b',
                    proc:         lambda { |v| @options[:action] = :status}
            option  :template,
                    short:        '-t',
                    long:         '--template FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary. Default #{StackerApplication.defaultoptions[:template_file]}",
                    proc:         lambda { |v| @options[:template_file] = v }
            option  :template_file,
                    long:         '--template-file FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary. Default #{StackerApplication.defaultoptions[:template_file]}",
                    default:      'templates/mvc-vpc.json'
            option  :template_file_alt,
                    long:         '--template_file FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary. Default #{StackerApplication.defaultoptions[:template_file]}",
                    proc:         lambda { |v| @options[:template_file] = v }
            option  :template_url_alt,
                    long:         '--template-url URL',
                    description:  'Specify the URL of a template stored in an S3 bucket to run. This method assumes the template has already been uploaded.',
                    proc:         lambda { |v| @options[:template_url] = v }
            option  :template_url,
                    long:         '--template_url URL',
                    description:  'Specify the URL of a template stored in an S3 bucket to run. This method assumes the template has already been uploaded.'
            option  :use_s3,
                    long:         '--use_s3',
                    description:  'Use an S3 bucket to upload the template to before attempting stack run. This method assumes the template needs to be uploaded and may overwrite a key with the same name.'
            option  :ssh_keyfile,
                    short:        '-k',
                    long:         '--keyfile FILE',
                    description:  'The path to the SSH key file to use for SSH to hosts'
            option  :ssh_user,
                    long:         '--ssh_user USER',
                    description:  'SSH User name',
                    default:      'ubuntu'
            option  :ssh_user_bastion,
                    long:         '--ssh_user_bastion USER',
                    description:  'Bastion SSH User name',
                    default:      'ubuntu'
            option  :initial_setup,
                    short:        '-i',
                    long:         '--initial_setup FILE',
                    description:  'An initial setup file for Ansible',
                    default:      'ansible/playbooks/initial_setup.yml'
            option  :output_path,
                    short:        '-o',
                    long:         '--output PATH',
                    description:  'Output folder for the Ansible build configuration'
            option  :roles_path,
                    short:        '-r',
                    long:         '--roles_path PATH',
                    description:  'Output folder for the Ansible build configuration',
                    default:      'ansible/playbooks/roles'
            option  :progress,
                    short:        '-p',
                    long:         '--progress',
                    description:  'Print a progress indicator during stack create/update/delete.',
                    default:      false
            option  :inifile,
                    short:        "-f",
                    long:         "--inifile FILE",
                    description:  "INI file with settings"
            option  :log_file_alt,
                    long:         '--log-file PATH',
                    description:  'Log destination file',
                    proc:         lambda { |v| @options[:log_file] = v }
            option  :log_file,
                    long:         '--log_file PATH',
                    description:  'Log destination file'
            option  :log_level_alt,
                    long:         '--log-level LEVEL',
                    description:  'Logging level',
                    proc:         lambda{|v|
                      if StackerApplication.loglevels.include? v.to_sym
                        v.to_sym
                      else
                        level = StackerApplication.loglevels.select{|l| l.to_s.match(%r(^#{v}))}
                        unless level.size > 0
                          raise ::OptionParser::InvalidOption.new("Invalid log level: #{v}. Valid levels are #{StackerApplication.loglevels.ai}")
                        end
                        level[0].to_sym
                      end
                    }
            option  :log_level,
                    long:         '--log_level LEVEL',
                    description:  'Logging level',
                    proc:         lambda{|v|
                      if StackerApplication.loglevels.include? v.to_sym
                        v.to_sym
                      else
                        level = StackerApplication.loglevels.select{|l| l.to_s.match(%r(^#{v}))}
                        unless level.size > 0
                          raise ::OptionParser::InvalidOption.new("Invalid log level: #{v}. Valid levels are #{StackerApplication.loglevels.ai}")
                        end
                        level[0].to_sym
                      end
                    }
            option  :report_config_alt,
                    long:         '--report-config',
                    description:  'Report Configuration',
                    proc:         lambda { |v| @options[:report_config] = true }
            option  :report_config,
                    long:         '--report-config',
                    description:  'Report Configuration'

          end # included
          # ------------------------------------------------------------------------------------------------------------

        end

      end
    end
  end
end