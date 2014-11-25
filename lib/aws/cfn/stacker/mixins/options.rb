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
          if options[:use_aws]
            %w(AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY).each { |k|
              missing[k] = true unless ENV.has_key?(k)
            }
          end
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

            usage: build.py [-h] [-c CONFIG_FILE] [--ec2-config EC2_CONFIG]
                            [--profile PROFILE] [-v] [--debug] [--trace]
                            [--log_level LOG_LEVEL] [--log_file LOG_FILE]
                            [--log_format LOG_FORMAT] [-d] [-r] [-y] [-n] [-w] [-g]
                            [--timestamp] [-l] [--list-stacks] [-t TEMPLATE]
                            [--template_url TEMPLATE_URL] [--use_s3] [--cloudfront]
                            [-k KEYFILE] [-b] [-a ./ansible]
                            [-i ansible/playbooks/initial_setup.yml] [-o OUTPUT]
                            [-R ansible/playbooks/roles]
                            [-V ansible/playbooks/roles/group_vars/all]
                            [--ssh_user SSH_USER] [--ssh_user_bastion SSH_USER_BASTION]
                            [-u] [-C] [-U] [--quick] [-s] [-p] [--compile]
                            [--specification SPECIFICATION] [--stack_path STACK_PATH]
                            [--brick_path BRICK_PATH] [--rvm_ruby RVM_RUBY]
                            [--rvm_gemset RVM_GEMSET]
                            STACK_NAME

            Build a Core Platform VPC

            positional arguments:
              STACK_NAME            A stack to operate on.

            optional arguments:
              -h, --help            show this help message and exit
              -c CONFIG_FILE, --config-file CONFIG_FILE
                                    An ini file to use that contains one section per
                                    stack, with all the parameters for the stack
                                    enumerated
              --ec2-config EC2_CONFIG
                                    An EC2 environment config file containing the
                                    credentials for accessing the AWS API
              --profile PROFILE     AWS credentials using the default config from environmetns .aws/config file to access AWS API
              -v, --verbose         Increase verbosity, can be specified multiple times
                                    (currently just sets the debug level for AWS)
              --debug               Increase debug, can be specified multiple times
              --trace               Increase tracing, can be specified multiple times
              --log_level LOG_LEVEL
                                    The logging level desired. One of warn,info,step,criti
                                    cal,trace,err,error,crit,debug,warning
              --log_file LOG_FILE   The log file to write log messages to
              --log_format LOG_FORMAT
                                    The logging format to write log messages in (See
                                    https://docs.python.org/2/howto/logging.html#changing-
                                    the-format-of-displayed-messages)
              -d, --delete          Delete the requested stack. WARNING: No second
                                    chances!
              -r, --remove          Delete the requested stack. WARNING: No second
                                    chances!
              -y, --yes             Answer yes to all confirmation promtps
              -n, --no              Answer no to all confirmation promtps
              -w, --watch           Watch the progress of the requested stack. Useful if a
                                    create/update/delete was interrupted.
              -g, --background      Run stack operation in the background. Useful if a
                                    create/update/delete will take a long time and you
                                    don't want to hold the terminal hostage.
              --timestamp           Put timestamp on progress log output.
              -l, --list-params     List the parameters in the template, and show what
                                    values are supplied by your config file
              --list-stacks         List the stacks in the configuration file
              -t TEMPLATE, --template TEMPLATE
                                    Specify a different template to run. Note that
                                    specific outputs are expected, so results may vary.
              --template_url TEMPLATE_URL
                                    Specify the key of a template stored in an S3 bucket
                                    to run. This method assumes the template has already
                                    been uploaded.
              --use_s3              Use an S3 bucket to upload the template to before
                                    attempting stack run. This method assumes the template
                                    needs to be uploaded and may overwrite a key with the
                                    same name.
              --cloudfront          Use this to include cloudfront resources in the stack.
              -k KEYFILE, --keyfile KEYFILE
                                    The path to the SSH key file to use for SSH to hosts
              -b, --build           Build configuration directory for use with Ansible.
              -a ./ansible, --ansible ./ansible
                                    Path to the 'ansible' directory ex. /Development
                                    /ansible-playbooks/ansible
              -i ansible/playbooks/initial_setup.yml, --initial_setup ansible/playbooks/initial_setup.yml
                                    An initial setup file for Ansible
              -o OUTPUT, --output OUTPUT
                                    Output folder for the Ansible build configuration
              -R ansible/playbooks/roles, --roles_path ansible/playbooks/roles
                                    Folder for the Ansible roles
              -V ansible/playbooks/roles/group_vars/all, --vars_file ansible/playbooks/roles/group_vars/all
                                    File with the Ansible vars common to all playbooks
              --ssh_user SSH_USER   SSH User name
              --ssh_user_bastion SSH_USER_BASTION
                                    Bastion SSH User name
              -u, --update          Update the configuration of an existing stack
              -C, --configure       Run pre-defined ansible playbooks against the given
                                    stack. Implies -b and -U
              -U, --upload          Upload Ansible stack configuration directory items if
                                    meta-coordinates specified in INI. Implies -b.
              --quick               Show the quick (no instance detail) status of a given
                                    stack.
              -s, --status          Print the current status of a given stack.
              -p, --progress        Print a progress indicator during stack
                                    create/update/delete.
              --compile             Compile template.
              --specification SPECIFICATION
                                    Template specification.
              --stack_path STACK_PATH
                                    Stack path.
              --brick_path BRICK_PATH
                                    Brick path.
              --rvm_ruby RVM_RUBY   RVM Gem set.
              --rvm_gemset RVM_GEMSET
                                    RVM Gem set.

            EOF

            option  :help,
                    short:        '-h',
                    long:         '--help',
                    description:  'Show this help message and exit',
                    show_options: true,
                    exit:         1
            # print the version.
            option  :version,
                    long:         '--version',
                    description:  'Show version',
                    proc:         Proc.new{ puts ::Aws::Cfn::Stacker::VERSION },
                    exit:         2
            option  :config_file_alt,
                    long:         '--config_file FILE',
                    description:  'A config file to use that contains one section per stack, with all the parameters for the stack enumerated.',
                    proc:         lambda{ |v|
                      @options[:config_file] = v
                    }
            option  :config_file,
                    short:        '-c',
                    long:         '--config-file FILE',
                    description:  'A config file which contains one section per stack.',
                    default:      'config/config.ini'
            option  :debug,
                    short:        '-D',
                    long:         '--debug',
                    description:  'Increase debug level, can be specified multiple times.'
            option  :yes,
                    short:        '-y',
                    long:         '--yes',
                    description:  'Answer yes to all confirmation promtps'
            option  :no,
                    short:        '-n',
                    long:         '--no',
                    description:  'Answer no to all confirmation promtps'
            option  :verbose,
                    short:        '-v',
                    long:         '--verbose',
                    description:  'Increase verbosity, can be specified multiple times.',
                    proc:         lambda {|v|
                      index = $STKR.class.loglevels.index(@options[:log_level]) || $STKR.class.loglevels.index(:warn)
                      index -= 1 if index > 0
                      @options[:log_level] = $STKR.class..loglevels[index]
                    }
            option  :debug,
                    long:         '--debug',
                    description:  'Increase debug level, can be specified multiple times.'
            option  :trace,
                    long:         '--trace',
                    description:  'Increase tracing, can be specified multiple times'
            option  :ansible,
                    short:        '-a',
                    long:         '--ansible',
                    description:  "Path to the 'ansible' directory ex. /Development /ansible-playbooks/ansible"
            # option  :actions,
            #         short:        '-A',
            #         long:         '--action ACTION',
            #         description:  "Perform the requested action against the stack. (#{StackerApplication.allactions.to_s})",
            #         proc:         lambda{|v|
            #           actions = $STKR.parseOptionString(v,',', 'parseActionSymbol')
            #           all     = [StackerApplication.allactions, :all].flatten
            #           actions.each{ |act|
            #             unless all.include?(act.to_sym)
            #               raise ::OptionParser::InvalidOption.new("Invalid action: #{act.to_s}. Valid actions are: #{all.to_s}")
            #             end
            #           }
            #           actions
            #         },
            #         default:      [ :create ]
            option  :build,
                    short:        '-b',
                    long:         '--build',
                    description:  'Build configuration directory for use with Ansible.',
                    proc:         lambda { |v| @options[:action] = :build}
            option  :compile,
                    long:         '--compile',
                    description:  'Compile template.',
                    proc:         lambda { |v| @options[:action] = :compile}
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
            option  :liststacks,
                    long:         '--list-stacks',
                    description:  'List the stacks in the configuration file',
                    proc:         lambda { |v| @options[:action] = :liststacks}
            option  :configure,
                    short:        '-C',
                    long:         '--configure',
                    description:  'Run pre-defined ansible playbooks against the given stack. Implies -b and -U',
                    proc:         lambda { |v| @options[:action] = :configure}
            option  :status,
                    short:        '-s',
                    long:         '--status',
                    description:  'Print the current status of a given stack.',
                    proc:         lambda { |v| @options[:action] = :status}
            option  :upload,
                    short:        '-U',
                    long:         '--upload',
                    description:  'Upload Ansible stack configuration directory items if meta-coordinates specified in INI. Implies -b.',
                    proc:         lambda { |v| @options[:action] = :upload}
            option  :watch,
                    short:        '-w',
                    long:         '--watch',
                    description:  'Watch the progress of the requested stack. Useful if a create/update/delete was interrupted.',
                    proc:         lambda { |v| @options[:action] = :watch}
            option  :timestamp,
                    long:         '--timestamp',
                    description:  'Put timestamp on progress log output.'
            option  :quick,
                    long:         '--quick',
                    description:  'Show the quick (no instance detail) status of a given stack.'
            option  :background,
                    short:        '-g',
                    long:         '--background',
                    description:  "Run stack operation in the background. Useful if a create/update/delete will take a long time and you don't want to hold the terminal hostage."
            option  :cloudfront,
                    long:         '--cloudfront',
                    description:  'Use this to include cloudfront resources in the stack. (Deprecated/ Ignored)'
            option  :template,
                    short:        '-t',
                    long:         '--template FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary.",
                    proc:         lambda { |v| @options[:template_file] = v }
            option  :template_file,
                    long:         '--template-file FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary.",
                    default:      'templates/mvc-vpc.json'
            option  :template_file_alt,
                    long:         '--template_file FILE',
                    description:  "Specify a template to run. Note that specific outputs are expected, so results may vary.",
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
            option  :specification,
                    long:         '--specification PATH',
                    description:  'Template specification.'
            option  :brick_path,
                    long:         '--brick_path PATH',
                    description:  'Brick path.'
            option  :stack_path,
                    long:         '--stack_path PATH',
                    description:  'Stack path.'
            option  :rvm_ruby,
                    long:         '--rvm_ruby PATH',
                    description:  'RVM Gem set.'
            option  :roles_path,
                    short:        '-R',
                    long:         '--roles_path PATH',
                    description:  'Folder for the Ansible roles',
                    default:      'ansible/playbooks/roles'
            option  :vars_file,
                    short:        '-V',
                    long:         '--vars_file PATH',
                    description:  'File with the Ansible vars common to all playbooks'
            option  :progress,
                    short:        '-p',
                    long:         '--progress',
                    description:  'Print a progress indicator during stack create/update/delete.',
                    default:      false
            option  :ec2_config_alt,
                    long:         '--ec2-config FILE',
                    description:  'An EC2 environment config file containing the credentials for accessing the AWS API'
            option  :ec2_config,
                    long:         '--ec2_config FILE',
                    description:  'An EC2 environment config file containing the credentials for accessing the AWS API'
            option  :inifile,
                    short:        '-f',
                    long:         '--inifile FILE',
                    description:  'INI file with settings'
            option  :profile,
                    long:         '--profile PROFILE',
                    description:  "AWS credentials using the default config from environments' .aws/config file to access AWS API."
            option  :log_file_alt,
                    long:         '--log-file PATH',
                    description:  'Log destination file',
                    proc:         lambda { |v| @options[:log_file] = v }
            option  :log_file,
                    long:         '--log_file PATH',
                    description:  'The log file to write log messages to'
            option  :log_format,
                    long:         '--log_format FORMAT',
                    description:  'The logging format to write log messages in (Not used)'
            option  :log_level_alt,
                    long:         '--log-level LEVEL',
                    description:  'Logging level. One of '+LOGLEVELS.join(', '),
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
                    description:  'Logging level. One of '+LOGLEVELS.join(', '),
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
                    },
                    default: :step
            option  :report_config_alt,
                    long:         '--report-config',
                    description:  'Report Configuration',
                    proc:         lambda { |v| @options[:report_config] = true }
            option  :report_config,
                    long:         '--report-config',
                    description:  'Report Configuration'

            option  :build_py,
                    long:         '--build-py BUILD_PY',
                    description:  'Evaluate option compatibility with build.py'
            option  :build_venv,
                    long:         '--build-venv VENV_PATH',
                    description:  'Virtual environment path for build.py'

          end # included
          # ------------------------------------------------------------------------------------------------------------

        end

      end
    end
  end
end