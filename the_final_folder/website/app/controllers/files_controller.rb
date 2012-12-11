require 'pathname'
unless defined?(Client) == 'constant' && Client.class == Class 
	require Dir.pwd + '/../client/client.jar'
	Client = JavaUtilities.get_proxy_class('client')
	Messaging = JavaUtilities.get_proxy_class('messaging')
end
class FilesController < ApplicationController
	#include SessionHelper
	include FilesHelper
	def show
		redirect_to '/domains'
	end
	def ls
		domain = Domain.find_by_name(params[:domain])
		user = User.find_by_remember_token(cookies[:remember_token]);
		redirect_to '/signin' and return if user.nil?
		puts ">>>>>>>>>>>>>Contains cd: #{params[:cd]} and is #{params[:cd]} : #{params[:cd] == "down"}"
		if(params[:cd] == "up" || params[:cd] == "down")
			cdir = cookies[:directory]
			cdir = Pathname.new(cdir + "/").parent.to_str if params[:cd] == "up"
			cdir = (Pathname.new(cdir) + params[:id]).to_str if params[:cd] == "down"
			cookies.permanent[:directory] = cdir
			redirect_to "/files/" + domain.name
			return
		end
		@domain_name = domain.name
		target_directory = '/' + cookies[:directory];		
		#	if target_directory.nil? || target_directory == 'null' || target_directory.empty?
		setup_client(domain,user,target_directory)
		puts "================== Attempting To LS Into Directory: #{target_directory} =========================="
		out = $client.ls([])
		@files_in_directory = []
		@files_in_directory = out.split() unless out.nil? 
		@the_current_directory = target_directory
		render 'folder'
	end
	def get_file
		domain = Domain.find_by_name(params[:domain])
		user = User.find_by_remember_token(cookies[:remember_token]);
                redirect_to '/signin' and return if user.nil?
		@target_file = cookies[:directory] + '/' + params[:id]
		puts "====================== Getting file : #{@target_file} ========"
		target_directory = '/' + cookies[:directory];		
		setup_client(domain,user,target_directory)
		if user.nil?
			redirect_to '/files' and return
		else
			render :format => :html,:template => 'files/download'
		end
		$client.get([ "data/output/" + params[:id], params[:id] ])
		return
	end
	def download_file
		file_path = Dir.pwd + '/data/output/' + File.basename(params[:id])
		if(Pathname.new(file_path).exist?)
			send_file file_path
		else
			render :text => "Unable to locate output file possible get failure"
		end
	end
	def put_file
		folder_name = params[:folder]
		up_file = params[:upload]
		name = params[:name]
		domain = Domain.find_by_name params[:domain]
		user = User.find_by_remember_token(cookies[:remember_token])
		if user.nil?
			redirect_to '/files' and return
		else
			redirect_to '/files/' + domain.name
		end
		target_directory = '/' + cookies[:directory]		
		setup_client(domain,user,target_directory)
		unless up_file.nil?
			puts "================ Writing file to local directory ====================="
			path = File.join("data/upload", name)
			File.open(path, "wb") { |f| f.write(up_file.read) }
			$client.put([ path, name ])
		else
			puts "================ Creating folder ====================="
			$client.mkdir([ folder_name ])
		end
		
	end
	def delete
		name = params[:id]
		domain = Domain.find_by_name params[:domain]
		user = User.find_by_remember_token(cookies[:remember_token])
                redirect_to '/signin' and return if user.nil?
		target_directory = '/' + cookies[:directory];		
		setup_client(domain,user,target_directory)
		if name.include? '/'
			puts "====================== Removing Directory : #{@target_file} ========"
			$client.rmdir([name])
		else
			puts "====================== Removing file : #{@target_file} ========"
			$client.rm([name])
		end
		redirect_to '/files/' + domain.name
	end
end
