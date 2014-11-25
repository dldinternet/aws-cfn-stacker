module Aws::Cfn::Stacker
  class ListStacks < StackerBase
    include Options

    banner "#{Aws::Cfn::Stacker::StackerApplication.cmd} list stacks (options)"

    def load_config_defaults(path=nil)
      {}
    end

    # Called prior to starting the application, by the run method
    def setup_application
      logStep "#{self.class.to_s}.#{__method__}"
      logger.debug "#{argv.join(' ')}"
    end

    # Actually run the application
    def run_application
      logStep "#{self.class.to_s}.#{__method__}"
    end
  end
end