class Domain < ActiveRecord::Base
  attr_accessible :name
  has_many :servers
end
