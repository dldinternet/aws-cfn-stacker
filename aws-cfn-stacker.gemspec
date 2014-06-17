# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'aws/cfn/stacker/version'

Gem::Specification.new do |spec|
  spec.name          = "aws-cfn-stacker"
  spec.version       = Aws::Cfn::Stacker::VERSION
  spec.authors       = ["Christo De Lange"]
  spec.email         = ["rubygems@dldinternet.com"]
  spec.summary       = %q{A CloudFormation stack management helper to do stack CRUD and chaining}
  spec.description   = %q{A CloudFormation stack management helper to do stack CRUD and chaining. It eases the pain of setting up parameters and chaining stacks.}
  spec.homepage      = ""
  spec.license       = "MIT"

  spec.files         = `git ls-files -z`.split("\x0")
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_dependency 'awesome_print'
  spec.add_dependency 'colorize'
  spec.add_dependency 'inifile'
  spec.add_dependency 'dldinternet-mixlib-cli', ">= 0.1.0", '~> 0.1'
  spec.add_dependency 'dldinternet-mixlib-logging', ">= 0.1.5", '~> 0.1'
  spec.add_dependency 'aws-cfn-yats', ">= 0.1.6", '~> 0.1'

  spec.add_development_dependency "bundler", "~> 1.6"
  spec.add_development_dependency "rake"
end
