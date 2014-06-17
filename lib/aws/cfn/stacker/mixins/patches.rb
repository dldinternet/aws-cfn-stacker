module Aws
  module Cfn
    module Stacker
      module Patches

      end
    end
  end
end

class ::TrueClass
  def to_rb
    to_s
  end
  def to_sym
    :true
  end
  def yesno
    "yes"
  end
end

class ::FalseClass
  def to_rb
    to_s
  end
  def to_sym
    :false
  end
  def yesno
    "no"
  end
end

module ::Logging
  class << self
    def levelnames=(lnames)
      remove_const(:LNAMES)
      const_set(:LNAMES, lnames)
    end
    def levelnames()
      LNAMES
    end
  end
end

