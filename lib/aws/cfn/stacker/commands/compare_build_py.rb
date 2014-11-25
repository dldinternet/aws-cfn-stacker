module Aws::Cfn::Stacker
  class CompareBuildPy < StackerBase

    # include ::Mixlib::CLI
    include Options

    banner "#{Aws::Cfn::Stacker::StackerApplication.cmd} compare build-py (options)"

    def load_config_defaults(path=nil)
      {}
    end

    # Called prior to starting the application, by the run method
    def setup_application
      logStep "#{self.class.to_s}.#{__method__}"
    end

    # Actually run the application
    def run_application
      logStep "#{self.class.to_s}.#{__method__}"

      venv = ''
      if @config[:build_venv]
        venv = ". #{File.expand_path(@config[:build_venv])}; "
      end
      py  = "bash -c '#{venv}#{@config[:build_py]} --help'"
      out = ''
      require 'open3'
      rc = 0
      env = ENV.to_hash.dup
      env['PYTHONUNBUFFERED'] = '1'
      Open3.popen3(env,py) do |stdin, stdout, stderr, wait_thr|
        stdout.sync = true
        # noinspection RubyAssignmentExpressionInConditionalInspection
        while line = stdout.gets
          logger.trace line.chomp
          out += line
        end
        stderr.sync = true
        # noinspection RubyAssignmentExpressionInConditionalInspection
        while line = stderr.gets
          logger.error line.chomp
        end
        rc = wait_thr.value
      end

      lines = out.split(%r'\n')

      lines.map! do |line|
        line.chomp
      end

      opt_arg = lines.index('optional arguments:')

      lines = lines[opt_arg+1..-1]

      help = lines[0]
      start = help[0..help.index('-')-1]
      help.gsub!(%r'^#{start}', '')
      parts = help.split(%r'   +')

      desi  = help.index(parts[1])
      map   = {}
      span  = false
      desc  = ''
      opts  = ''
      spac  = ''
      lino  = 1

      lines.map do |line|
        # Chop of leading wasted space
        line.gsub!(%r'^#{start}', '')
        # if we are in a description span ...
        if span
          # And it is a continuation
          if line.match(%r'^\s+')
            desc = "#{desc}#{spac}#{line.gsub(%r'^\s+', '')}"
            spac = ' '
          else
            # Otherwise a new option
            span = false
            map_option(lino, map, opts, desc)
            desc = ''
          end
        end
        # Are out of a description span?
        unless span
          if line.match(%r'^\s+')
            span = true
            spac = ' '
            desc = "#{desc}#{spac}#{line.gsub(%r'^\s+', '')}"
          else
            logger.debug "#{lino}: #{line}"
            # Does this line have a space before the description column ?
            if line[desi-1] == ' '
              opts = get_opts(line[0..desi-1], desi)
              desc = line[desi..-1]
              if lines[lino].match(%r'^  \s+')
                span = true
                spac = ' '
                if line[0..desi-1].match(/,/) and (not line[desi..-1].match(/\s+/))
                  opts = get_opts(line[0..-1], desi)
                  desc = ''
                  spac = ''
                end
              else
                # Simple case
                map_option(lino, map, opts, desc)
                desc = ''
              end
            else
              # A spanning description case
              span = true
              opts = get_opts(line[0..-1], desi)
              desc = ''
              spac = ''
            end
          end
        end
        lino += 1
      end

    end

    private

    def get_opts(line, desi)
      opts = line.gsub(%r'^\s+', '').gsub(%r'\s+$', '').split(%r',\s*')
      opts.map! do |opt|
        opt.split.shift
      end
      opts
    end

    def map_option(lino, map, opts, desc)
      ours = @options.select { |sym, hsh|
        long = hsh[:long] ? (hsh[:long].split.shift) : nil
        (hsh[:short] and opts.include?(hsh[:short])) or (long and opts.include?(long))
      }
      # Did we find a matching option on our side ?
      if ours.size == 1
        map[ours.keys[0]] = [lino,desc]
        logger.debug "Map option: #{lino}: #{opts.join(',')} ==> #{ours.keys[0]}"
        logger.warn  "#{lino}: #{opts.join(',')} '#{ours[ours.keys[0]][:description]}' vs '#{desc}'" unless (ours[ours.keys[0]][:description].downcase == desc.downcase)
      elsif ours.size > 1
        logger.error "Mapped multiple options: #{lino}: #{opts.join(',')} ==> #{ours.keys.join(',')}"
      else
        logger.error "Failed to map option: #{lino}: #{opts.join(',')}"
      end
    end

  end
end
