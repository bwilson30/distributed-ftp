require 'rubygems'
require 'java'
#require Dir.pwd + '/../client/client.jar'
#Client = JavaUtilities.get_proxy_class('client')
#Messaging = JavaUtilities.get_proxy_class('messaging')
# Set up gems listed in the Gemfile.
ENV['BUNDLE_GEMFILE'] ||= File.expand_path('../../Gemfile', __FILE__)

require 'bundler/setup' if File.exists?(ENV['BUNDLE_GEMFILE'])
