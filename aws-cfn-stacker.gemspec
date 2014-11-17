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
  spec.homepage      = ''
  spec.license       = 'Apache2'

  spec.files         = `git ls-files -z`.split("\x0")
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_dependency 'awesome_print'
  spec.add_dependency 'colorize'
  spec.add_dependency 'inifile'
  spec.add_dependency 'slop'

  spec.add_dependency 'aws-cfn-dsl',        '>= 0.9.3'
  spec.add_dependency 'aws-cfn-compiler',   '>= 0.9.4'
  spec.add_dependency 'aws-cfn-decompiler', '>= 0.9.1'
  spec.add_dependency 'dldinternet-mixlib-logging', '>= 0.4.1'
  spec.add_dependency 'mixlib-cli', '> 0'
  spec.add_dependency 'dldinternet-mixlib-cli', '>= 0.2.0'

end
