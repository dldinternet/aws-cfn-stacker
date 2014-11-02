module Aws
  module Cfn
    module Stacker

      module Prerequisites
        attr :checkRuby

        # -----------------------------------------------------------------------------
        @TMPDIR = nil
        def getTmpDir
          unless @TMPDIR
            @TMPDIR = ENV.key?('TMPDIR') ? ENV['TMPDIR'] : '/tmp/'
          end
          @TMPDIR
        end

        # -----------------------------------------------------------------------------
        def checkOS()
          require 'rbconfig'
          #OS.mac?
          unless RbConfig::CONFIG['host_os'] =~ %r/darwin/
            raise ::Aws::Cfn::Stacker::Errors::SetupError.new("Sorry! Your OS platform (#{RUBY_PLATFORM}) is not yet supported (as of 2014/06/16)!")
          end
        end

        # -----------------------------------------------------------------------------
        def getChefPath(logger)
          chef = %x(which chef-solo 2>/dev/null).gsub(%r/\s+/, '')
          unless chef == ''
            logger.debug "Found chef-solo: '#{chef}'" if logger
            if File.symlink?(chef)
              chef = File.readlink(chef)
            end
            stat = File.stat(chef)
            chef = File.dirname(chef)
          end
          chef
        end

        # -----------------------------------------------------------------------------
        def askForChefRuby(must_chef=true)
          return unless must_chef
          embedded_bin = File.expand_path(getChefPath(false)+"/../embedded/bin")
          puts "Please reissue the command as follows: "
          aCmd = ["#{embedded_bin}/ruby", $0]
          aCmd << ARGV.dup
          aCmd.flatten!
          puts aCmd.join(' ')
          exit -1
        end

        # -----------------------------------------------------------------------------
        def findChefRuby()
          embedded_bin = File.expand_path(getChefPath(false)+"/../embedded/bin")
          embedded_ruby = "#{embedded_bin}/ruby"
          if File.executable?(embedded_ruby)
            ruby_version = "\x1b[$38;5;1m"+%x(#{embedded_ruby} -v 2>/dev/null)+"\x1b[0m" # .gsub(%r/\s+/, '')
            puts "You have a Chef embedded Ruby ... #{ruby_version} ...\n\nYou should place it on the PATH (export PATH=#{embedded_bin}#{File::PATH_SEPARATOR}$PATH) or use it directly."
            return 0
          end
          1
        end

        # -----------------------------------------------------------------------------
        def yieldChefRuby(&block)
          if 0 == findChefRuby()
            askForChefRuby(false)
          else
            yield
          end
        end

        # -----------------------------------------------------------------------------
        def checkRuby()
          @checkRuby = 0 if @checkRuby.nil?

          if RUBY_VERSION.match(%r/^(1\.9|2\.)/)
            begin
              ENV.delete('GEM_PATH')
              ENV.delete('GEM_HOME')
              require 'chef/version'
            rescue LoadError => e
              puts 'Your Ruby is new enough but you do not seem to have the Chef gem installed!'
              yieldChefRuby {
                puts 'Chef is not essential right now ... ignoring'
              }
            end
          else
            puts "Sorry! Your Ruby version (#{RUBY_VERSION}) is too low!\nYou need at least 1.9.3."

            yieldChefRuby {
              if ARGV.include?('--install-chef')
                installChef()
                puts "Checking Ruby again ... "
                @checkRuby += 1
                checkRuby() unless @checkRuby > 1
              else
                puts "

You can get a newer version of Ruby with Chef Omnibus installer.

Option 1: Provide the --install-chef option

Option 2: Install Chef yourself
1) Install Chef with: 'curl -L https://www.opscode.com/chef/install.sh | sudo bash'
2) Run this script with: '/opt/chef/embedded/bin/ruby #{$0} #{ARGV.clone.join(' ')}'


"
                raise ::Aws::Cfn::Stacker::Errors::SetupError.new("Sorry! Your Ruby version (#{RUBY_VERSION}) is too low!")
              end
            }
          end

        end

        # -----------------------------------------------------------------------------
        def installChef()
          STDOUT.sync = true
          STDERR.sync = true
          #logger = getLogger(ARGV.hash)
          chef_installer = 'chef-11.6.0_1.mac_os_x.10.7.2.sh'
          tmp = getTmpDir()
          url = "https://opscode-omnibus-packages.s3.amazonaws.com/mac_os_x/10.7/x86_64/#{chef_installer}"
          puts "Installing Chef 11.6.0_1 from #{url}"
          aCmd = [ 'curl', '--silent', '-o', "#{tmp}/#{chef_installer}", "-L", url ]
          puts aCmd.join(' ')
          #rc = executeTask( aCmd, { :rc => 0, :msg => 'Success' }, [] )
          cond = {
              :logit            => true,
              :rc               => 0,
              :msg              => 'Success',
          }

          pid = fork do
            ret = system(aCmd.join(' '))
            #logger.debug "[child] system(): #{ret}"
            exit ret === true ? 0 : -1
          end

          pid,status = Process.waitpid2(pid)
          rc = status.exitstatus
          if rc != 0
            cond[:msg] = "The uninstaller did not complete or did not succeed possibly because administrative privileges were not authorized."
          end

          raise ::Aws::Cfn::Stacker::Errors::SetupError.new("Sorry, cannot download Chef installer") unless rc == 0

          puts "\nI am going to execute a sudo command now: 'sudo #{tmp}/#{chef_installer}'\nPlease type your sudo password when prompted!\n"
          aCmd = [ 'sudo', 'bash', "#{tmp}/#{chef_installer}" ]
          puts aCmd.join(' ')
          ENV.delete('GEM_PATH')
          ENV.delete('GEM_HOME')
          #rc = executeTask( aCmd, { :rc => 0, :msg => 'Success' }, [] )

          cond = {
              :logit            => true,
              :rc               => 0,
              :msg              => 'Success',
          }

          pid = fork do
            ret = system(aCmd.join(' '))
            #logger.debug "[child] system(): #{ret}"
            exit ret === true ? 0 : -1
          end

          pid,status = Process.waitpid2(pid)
          rc = status.exitstatus
          if rc != 0
            cond[:msg] = "The uninstaller did not complete or did not succeed possibly because administrative privileges were not authorized."
          end

          raise ::Aws::Cfn::Stacker::Errors::SetupError.new("Sorry, cannot install Chef") unless rc == 0

          # Get control back of this new installation
          puts "We use sudo and may ask you for your password ..."
          puts %x(sudo chown -R #{ENV['USER']} /opt/chef)
          puts %x(sudo chmod -R u+w /opt/chef)

          rc
        end

      end

    end
  end
end
