unless defined?(Client) == 'constant' && Client.class == Class 
	require Dir.pwd + '/../client/client.jar'
	Client = JavaUtilities.get_proxy_class('client')
end
class User < ActiveRecord::Base
  attr_accessible :name, :password
  before_save :create_remember_token


    def authenticate(pass)
	return self.remember_token == Client.generate_userhash(self.name,pass)
    end
private
    def create_remember_token
        self.remember_token = Client.generate_userhash(self.name,self.password)
    end

end
