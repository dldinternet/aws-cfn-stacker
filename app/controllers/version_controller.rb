class VersionController < ApplicationController
  def index
    @versioninfo = VersionHelper::VersionInfo.createVersionInfo()

    # [2014-11-03 Christo] The respond_to only seems to look at Accept: ...
    # respond_to do |format|
    #   format.html { render plain: @versioninfo.to_s }
    #   format.json { render json: @versioninfo.to_h.to_json }
    #   format.js   { render json: @versioninfo.to_h.to_json }
    # end

    # [2014-11-03 Christo] There is opportunity here to put this in a base class ...
    content_type = Mime::Type.lookup(env['CONTENT_TYPE'])
    unless content_type
      content_type = Mime::Type.parse(env['HTTP_ACCEPT'])
      content_type = content_type.shift if content_type.is_a?(Array)
    end
    format       = if params[:format]
                     params[:format].gsub(%r'^/*','').to_sym
                   else
                     content_type ? content_type.to_sym : :html
                   end
    case format
      when :json
        render json: @versioninfo.to_h.to_json
      when :xml
        render xml: @versioninfo.to_h.to_xml
      else
        render plain: @versioninfo.to_s
    end
  end
end
