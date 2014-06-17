module Aws
  module Cfn
    module Stacker
      module Errors

        class StackerError < StandardError ; end
        class SetupError < StackerError ; end
        class ApplicationError < StackerError ; end

      end
    end
  end
end