class AddDomainNameToServer < ActiveRecord::Migration
  def change
    add_column :servers, :domain_name, :string
  end
end
