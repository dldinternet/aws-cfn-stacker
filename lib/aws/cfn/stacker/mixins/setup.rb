module Aws
  module Cfn
    module Stacker
      module Setup
        ENV.delete('GEM_PATH')
        ENV.delete('GEM_HOME')

        # Standard library gems
        require "optparse"
        require 'erb'

        GEMS = [
            "awesome_print",
            "colorize",
            "inifile",
            "logging",
            "chef",
        ]

        # =============================================================================
        # Check for gems we need
        require "rubygems"
        require 'rubygems/gem_runner'
        require 'rubygems/exceptions'
        GEMS.each{ |g|
          begin
            gem g
          rescue Gem::LoadError
            # not installed
            #puts %x(gem install #{g})
            begin
              puts "Need to install #{g}"
              args = ['install', g, '--no-rdoc', '--no-ri']
              Gem::GemRunner.new.run args
            rescue Gem::SystemExitException => e
              unless e.exit_code == 0
                puts "ERROR: Failed to install #{g}. #{e.message}"
                raise e
              end
            end
          end
        }

        # Add-on gems
        GEMS.map{ |g| require g }


        #############################################################
        ## FUNCTIONS

        ## FUNCTIONS
        #############################################################

      end

    end
  end
end
