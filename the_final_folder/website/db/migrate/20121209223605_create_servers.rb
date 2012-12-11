class CreateServers < ActiveRecord::Migration
  def change
    create_table :servers do |t|
      t.string :ip
      t.integer :domain_id

      t.timestamps
    end
  end
end
