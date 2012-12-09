unless defined?(Client) == 'constant' && Client.class == Class 
	require Dir.pwd + '/../client/client.jar'
	Client = JavaUtilities.get_proxy_class('client')
	Messaging = JavaUtilities.get_proxy_class('messaging')
end
class FilesController < ApplicationController
	#include SessionHelper
	def ls
		user = User.find_by_remember_token(cookies[:remember_token]);
		redirect_to '/signin' and return if user.nil?
		target_directory = cookies[:directory];		
		target_directory = "" if target_directory.nil? || target_directory == 'null' || target_directory.empty?
		puts "================== Initializing Client: #{user.name} :: #{target_directory.inspect} =========================="
		if $client.nil?
			$client = Client.new(1,10001) and puts "created client" 
		 	puts "================== Attempting login =========================="
			$client.login(user.name,user.password);
		end
		$client.rwd = target_directory;
		puts "================== Setting Domain login =========================="
		$client.set_domain(["127.0.0.1"]);
		$client.temp_folder= "data/temp"
		$client.config([])
		puts "================== Attempting To LS Into Directory: #{target_directory} =========================="
		$client.ls([])
		if($client.qur_size == 1)
			outdir = Dir.pwd + "/data/temp/0_ls.txt"	
		else
			outdir = $client.ls_file_path()
		end
		fi= File.new(outdir, "r")
		@files_in_directory = []
		while (line = fi.gets)
		      @files_in_directory.push "#{line}"
		end
		@the_current_directory = target_directory
		render 'folder'
	end
	def get_file
		user = User.find_by_remember_token(cookies[:remember_token]);
                redirect_to '/signin' and return if user.nil?
		@target_file = cookies[:directory] + '/' + params[:id]
		puts "====================== " +  @target_file
		if $client.nil?
			redirect_to '/files' and return
		else
			render :format => :html,:template => 'files/download'
		end
		$client.set_domain(["127.0.0.1"])
		$client.temp_folder= "data/temp"
		$client.rwd = cookies[:directory]
		$client.get([ "data/output/" + params[:id], params[:id] ])
		return
	end
	def download_file
		send_file 'data/output/' + params[:id]
	end
end
