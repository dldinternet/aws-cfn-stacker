#require 'helpers/version_helper'

class VersionController < ApplicationController
  def index
    @versioninfo = VersionHelper::VersionInfo.createVersionInfo()
    respond_to do |format|
      format.html { render html: @versioninfo }
      format.json { render json: @versioninfo }
      format.js   { raise format }
    end
  end
end
