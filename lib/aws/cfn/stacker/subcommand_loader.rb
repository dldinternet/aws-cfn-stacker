module Aws::Cfn::Stacker
  class SubcommandLoader

    attr_reader :lib_stacker_dir
    attr_reader :env

    def initialize(config_dir, env=ENV)
      @config_dir, @env = config_dir, env
      @forced_activate = {}
    end

    # Load all the sub-commands
    def load_commands
      subcommand_files.each { |subcommand| Kernel.load subcommand }
      true
    end

    def site_subcommands
      user_specific_files = []
      user_specific_files.concat Dir.glob(File.expand_path("commands/*.rb", File.dirname(__FILE__)))
      user_specific_files
    end

    def subcommand_files
      @subcommand_files ||= ([] + site_subcommands).flatten.uniq
    end

  end
  
end