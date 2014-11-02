require "aws/cfn/stacker/version"
require 'aws/cfn/stacker/subcommand_loader'
require 'aws/cfn/stacker/mixins/convert_to_class_name'
require 'mixlib/cli'

module Aws
  module Cfn
    module Stacker
      class StackerBase

        include ::Mixlib::CLI

        extend Aws::Cfn::Stacker::ConvertToClassName

        # ClassMethods
        class << self
          include ::Mixlib::CLI::ClassMethods

          # noinspection RubyClassVariableUsageInspection
          def self.reset_config_path!
            @@stacker_config_dir = nil
          end

          reset_config_path!
          # search upward from current_dir until .chef directory is found
          # noinspection RubyClassVariableUsageInspection
          def stacker_config_dir
            if @@stacker_config_dir.nil? # share this with subclasses
              @@stacker_config_dir = false
              full_path = Dir.pwd.split(File::SEPARATOR)
              (full_path.length - 1).downto(0) do |i|
                candidate_directory = File.join(full_path[0..i] + ["config" ])
                if File.exist?(candidate_directory) && File.directory?(candidate_directory)
                  @@stacker_config_dir = candidate_directory
                  break
                end
              end
            end
            @@stacker_config_dir
          end
          
          def reset_subcommands!
            @subcommands = {}
            @subcommands_by_category = nil
          end

          def inherited(subclass)
            unless subclass.unnamed?
              subcommands[subclass.snake_case_name] = subclass
            end
          end

          # Explicitly set the category for the current command to +new_category+
          # The category is normally determined from the first word of the command
          # name, but some commands make more sense using two or more words
          # ===Arguments
          # new_category::: A String to set the category to (see examples)
          # ===Examples:
          # Data bag commands would be in the 'data' category by default. To put them
          # in the 'data bag' category:
          #   category('data bag')
          def category(new_category)
            @category = new_category
          end

          def subcommand_category
            @category || snake_case_name.split('_').first unless unnamed?
          end

          def snake_case_name
            convert_to_snake_case(name.split('::').last) unless unnamed?
          end

          def common_name
            snake_case_name.split('_').join(' ')
          end

          # Does this class have a name? (Classes created via Class.new don't)
          def unnamed?
            name.nil? || name.empty?
          end

          def subcommand_loader
            @subcommand_loader ||= SubcommandLoader.new(stacker_config_dir)
          end

          def load_commands
            @commands_loaded ||= subcommand_loader.load_commands
          end

          def subcommands
            @@subcommands ||= {}
          end

          def subcommands_by_category
            unless @subcommands_by_category
              @subcommands_by_category = Hash.new { |hash, key| hash[key] = [] }
              subcommands.each do |snake_cased, klass|
                @subcommands_by_category[klass.subcommand_category] << snake_cased
              end
            end
            @subcommands_by_category
          end

          # Print the list of subcommands knife knows about. If +preferred_category+
          # is given, only subcommands in that category are shown
          def list_commands(preferred_category=nil)
            load_commands

            category_desc = preferred_category ? preferred_category + " " : ''
            puts "Available #{category_desc}subcommands: (for details, #{@stacker_cmd} SUB-COMMAND --help)\n\n"

            if preferred_category && subcommands_by_category.key?(preferred_category)
              commands_to_show = {preferred_category => subcommands_by_category[preferred_category]}
            else
              commands_to_show = subcommands_by_category
            end

            commands_to_show.sort.each do |category, commands|
              next if category =~ /deprecated/i
              puts "** #{category.upcase} COMMANDS **"
              commands.sort.each do |command|
                puts subcommands[command].banner if subcommands[command]
              end
              puts
            end
          end

          def dependency_loaders
            @dependency_loaders ||= []
          end

          def requires(&block)
            dependency_loaders << block
          end

          def load_deps
            dependency_loaders.each do |dep_loader|
              dep_loader.call
            end
          end

          def subcommand_class_from(args)
            command_words = args.select {|arg| arg =~ /^(([[:alnum:]])[[:alnum:]\_\-]+)$/ }

            subcommand_class = nil

            while ( !subcommand_class ) && ( !command_words.empty? )
              snake_case_class_name = command_words.join("_")
              subcommand_class = subcommands[snake_case_class_name]
              unless subcommand_class
                command_words.pop
              end
            end
            # see if we got the command as e.g., knife node-list
            subcommand_class ||= subcommands[args.first.gsub('-', '_')]
            subcommand_class || subcommand_not_found!(args)
          end

          # :nodoc:
          # Error out and print usage. probably becuase the arguments given by the
          # user could not be resolved to a subcommand.
          def subcommand_not_found!(args)
            puts "Cannot find sub command for: '#{args.join(' ')}'"

            if category_commands = guess_category(args)
              list_commands(category_commands)
            else
              list_commands
            end

            exit 10
          end

          def guess_category(args)
            category_words = args.select {|arg| arg =~ /^(([[:alnum:]])[[:alnum:]\_\-]+)$/ }
            category_words.map! {|w| w.split('-')}.flatten!
            matching_category = nil
            while (!matching_category) && (!category_words.empty?)
              candidate_category = category_words.join(' ')
              matching_category = candidate_category if subcommands_by_category.key?(candidate_category)
              matching_category || category_words.pop
            end
            matching_category
          end

          attr_accessor :stacker_cmd
          def cmd=(cmd)
            @stacker_cmd = cmd
          end

        end # ClassMethods

        # Called prior to starting the application, by the run method
        def setup_application
          logger.warn "#{self.to_s}: you must override setup_application"
        end

        # Actually run the application
        def run_application
          logger.warn "#{self.to_s}: you must override run_application"
        end

      end
    end
  end
end
