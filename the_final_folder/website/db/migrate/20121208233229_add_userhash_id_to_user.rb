class AddUserhashIdToUser < ActiveRecord::Migration
  def change
    add_column :users, :userhash, :string
  end
end
