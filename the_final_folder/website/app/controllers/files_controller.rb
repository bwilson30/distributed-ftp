unless defined?(Client) == 'constant' && Client.class == Class 
	require Dir.pwd + '/../client/client.jar'
	Client = JavaUtilities.get_proxy_class('client')
	Messaging = JavaUtilities.get_proxy_class('messaging')
end
class FilesController < ApplicationController
	#include SessionHelper
	def show
		redirect_to '/domains'
	end
	def ls
		domain = Domain.find_by_name(params[:domain])
		user = User.find_by_remember_token(cookies[:remember_token]);
		redirect_to '/signin' and return if user.nil?
		@domain_name = domain.name
		target_directory = cookies[:directory];		
		target_directory = "" if target_directory.nil? || target_directory == 'null' || target_directory.empty?
		puts "================== Initializing Client: #{user.name} :: #{target_directory.inspect} =========================="
		server_list = []
		domain.servers.each do |serv|
			server_list.push serv.ip
		end
		if $client.nil?
			$client = Client.new(1,10001) and puts "created client" 
		end
		$client.set_domain(server_list);
		puts "================== Attempting login =========================="
		$client.login(user.name,user.password);
		$client.rwd = target_directory;
		puts "================== Setting Domain login =========================="
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
		domain = Domain.find_by_name(params[:domain])
		user = User.find_by_remember_token(cookies[:remember_token]);
                redirect_to '/signin' and return if user.nil?
		@target_file = cookies[:directory] + '/' + params[:id]
		server_list = []
		domain.servers.each do |serv|
			server_list.push serv.ip
		end
		puts "====================== " +  @target_file
		if $client.nil?
			redirect_to '/files' and return
		else
			render :format => :html,:template => 'files/download'
		end
		$client.set_domain(server_list)
		puts "================== Attempting login =========================="
		$client.login(user.name,user.password);
		$client.config([])
		$client.temp_folder= "data/temp"
		$client.rwd = cookies[:directory]
		$client.get([ "data/output/" + params[:id], params[:id] ])
		return
	end
	def download_file
		if(Pathname.new(Dir.pwd + '/data/output' + params[:id]).exist?)
			send_file Dir.pwd + '/data/output' + params[:id]
		else
			render :text => "Unable to locate output file possible get failure"
		end
	end
	def put_file
		up_file = params[:upload]
		name = params[:name]
		domain = Domain.find_by_name params[:domain]
		user = User.find_by_remember_token(cookies[:remember_token])
		puts "================ Writing file to local directory ====================="
		path = File.join("data/upload", name)
		File.open(path, "wb") { |f| f.write(up_file.read) }
		if $client.nil?
			redirect_to '/files' and return
		else
			redirect_to '/files/' + domain.name
		end
		server_list = []
		domain.servers.each do |serv|
			server_list.push serv.ip
		end
		$client.set_domain(server_list)
		puts "================== Attempting login =========================="
		$client.login(user.name,user.password);
		$client.config([])
		$client.temp_folder= "data/upload"
		$client.rwd = cookies[:directory]
		$client.put([ path, name ])
		
	end
end
