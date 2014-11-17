source 'https://rubygems.org'

# Specify your gem's dependencies in aws-cfn-stacker.gemspec
gemspec

# Bundle edge Rails instead: gem 'rails', github: 'rails/rails'
gem 'rails', '~> 4.1'
# Use sqlite3 as the database for Active Record
gem 'sqlite3'
# Use SCSS for stylesheets
gem 'sass-rails', '~> 4.0.3'
# Use Uglifier as compressor for JavaScript assets
gem 'uglifier', '>= 1.3.0'
# Use CoffeeScript for .js.coffee assets and views
gem 'coffee-rails', '~> 4.0.0'
# See https://github.com/sstephenson/execjs#readme for more supported runtimes
# gem 'therubyracer',  platforms: :ruby

# Use jquery as the JavaScript library
gem 'jquery-rails'
# Turbolinks makes following links in your web application faster. Read more: https://github.com/rails/turbolinks
gem 'turbolinks'
# Build JSON APIs with ease. Read more: https://github.com/rails/jbuilder
gem 'jbuilder', '~> 2.0'
# bundle exec rake doc:rails generates the API under doc/api.
gem 'sdoc', '~> 0.4.0', :group => :doc

# Spring speeds up development by keeping your application running in the background. Read more: https://github.com/rails/spring
gem 'spring', :group => :development

# Use ActiveModel has_secure_password
# gem 'bcrypt', '~> 3.1.7'

# Use unicorn as the app server
# gem 'unicorn'

# Use Capistrano for deployment
# gem 'capistrano-rails', group: :development

# Use debugger
# gem 'debugger', group: [:development, :test]

gem 'cloudformation-ruby-dsl',    :path => '../cloudformation-ruby-dsl', :group => :development

gem 'dldinternet-mixlib-logging', :path => '../dldinternet-mixlib-logging', :group => :development
# gem 'dldinternet-mixlib-cli',     :path => '../dldinternet-mixlib-cli', :group => :development
# gem 'aws-cfn-dsl',                :path => '../aws-cfn-dsl', :group => :development
# gem 'aws-cfn-decompiler',         :path => '../aws-cfn-decompiler', :group => :development
# gem 'aws-cfn-compiler',           :path => '../aws-cfn-compiler', :group => :development

gem 'chef', '~> 11.16'

group :development, :test do
  gem 'rspec'
  gem 'rspec-core'
  gem 'rspec-rails', '~> 3'
end
