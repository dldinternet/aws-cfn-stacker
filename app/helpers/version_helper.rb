require 'aws/cfn/stacker/version'

module VersionHelper

=begin
 /**
  * A bean for holding version info
  */
=end
# noinspection RubyInstanceVariableNamingConvention,RubyDefParenthesesInspection
class VersionInfo
    def initialize(version,release,date)
      @m_version = version
      @m_release = release
      @m_date    = date
    end

    # Gets the version
    #
    # @return the version
    def getVersion()
        @m_version
    end

    def getUser()
        @m_user
    end

    def getRelease()
        @m_release
    end

    def getDate()
        @m_date
    end

    # noinspection RubyClassMethodNamingConvention,RubyClassVariableNamingConvention,RubyClassVariableUsageInspection,RubyDefParenthesesInspection
    class << self
      def createVersionInfo()
        @@g_versionInfo = VersionInfo.new(::Aws::Cfn::Stacker::VERSION, ::Aws::Cfn::Stacker::RELEASE, DateTime.now.strftime('%Y/%m/%d %H:%M:%S'))
      end
    end

    def to_s
      <<-EOS
Version: #{@m_version}
Release: #{@m_release}
Date: #{@m_date}
      EOS
    end

    def to_h
      Hash[ :Version, @m_version, :Release, @m_release, :Date, @m_date ]
    end

    private

    attr :m_version
    attr :m_release
    attr :m_date

    def m_date=(d)
      @m_date = d
    end

    def m_release=(r)
      @m_release = r
    end

    def m_version=(v)
      @m_version = v
    end
  end
end
