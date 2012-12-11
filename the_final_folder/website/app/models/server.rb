class Server < ActiveRecord::Base
  attr_accessible :domain_id, :ip, :domain_name
  belongs_to :domain
end
