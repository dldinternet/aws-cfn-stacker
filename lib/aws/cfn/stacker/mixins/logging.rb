require 'dldinternet/mixlib/logging'

module Aws
  module Cfn
    module Stacker
      module Logging
        include ::DLDInternet::Mixlib::Logging

        def self.included(receiver)

          receiver.extend(::DLDInternet::Mixlib::Logging::ClassMethods)

        end

      end
    end
  end
end