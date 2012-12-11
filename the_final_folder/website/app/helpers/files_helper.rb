module FilesHelper
	def setup_client(domain,user,target_directory)
		server_list = []
		domain.servers.each do |serv|
			server_list.push serv.ip
		end
		if $client.nil?
			$client = Client.new(1,10001) 
		puts "================== Created Client ======================================================" 
		end
		puts "================== Initializing Client: #{user.name} :: #{target_directory.inspect} ===="
		$client.rwd = target_directory;
		$client.temp_folder= "data/temp"
		puts "================== Setting Domain ======================================================"
		$client.set_domain(server_list);
		$client.config([])
		puts "================== Attempting login ===================================================="
		$client.login(user.name,user.password)
	end
end
