# Setup
%w(Patches Errors Prerequisites ).each { |m|
  require "aws/cfn/stacker/mixins/#{m.downcase}"
  eval "include Aws::Cfn::Stacker::#{m}"
}

checkOS()
checkRuby()

require "aws/cfn/stacker/version"
require "aws/cfn/stacker/base"

module Aws
  module Cfn
    module Stacker
      # noinspection RubyTooManyInstanceVariablesInspection,RubyTooManyMethodsInspection
      class StackerApplication < StackerBase

        require 'dldinternet/mixlib/cli'
        include ::DLDInternet::Mixlib::CLI

        %w(Main Options Logging).each { |m|
          require "aws/cfn/stacker/mixins/#{m.downcase}"
          eval "include Aws::Cfn::Stacker::#{m}"
        }

      end
    end
  end
end
