# Setup
%w(Patches Errors Prerequisites ).each { |m|
  # noinspection RubyResolve
  require "aws/cfn/stacker/mixins/#{m.downcase}"
  eval "include Aws::Cfn::Stacker::#{m}"
}

checkOS()
checkRuby()

require 'aws/cfn/stacker/version'
require 'aws/cfn/stacker/base'

module Aws
  module Cfn
    module Stacker
      # noinspection RubyTooManyInstanceVariablesInspection,RubyTooManyMethodsInspection,RubyResolve
      class StackerApplication < StackerBase
        include ::Mixlib::CLI

        %w(Main Options).each { |m|
          require "aws/cfn/stacker/mixins/#{m.downcase}"
          eval "include Aws::Cfn::Stacker::#{m}"
        }

        # Called prior to starting the application, by the run method
        def setup_application
          if @in_setup_application
            logger.error "Internal: Recursive call to #{__method__}"
            exit -1
          end
          @in_setup_application = true
          StackerApplication.actors.each {|_,instance|
            instance.setup_application
          }
          @in_setup_application = false
        end

        # Actually run the application
        def run_application
          if @in_run_application
            logger.error "Internal: Recursive call to #{__method__}"
            exit -1
          end
          @in_run_application = true
          StackerApplication.actors.each {|_,instance|
            instance.run_application
          }
          @in_run_application = false
        end

      end
    end
  end
end
