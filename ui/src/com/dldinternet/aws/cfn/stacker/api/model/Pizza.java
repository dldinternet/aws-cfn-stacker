/**
 * Copyright (C) 2009-2012 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dldinternet.aws.cfn.stacker.api.model;

import com.dldinternet.aws.cfn.stacker.api.ApiBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class Pizza extends ApiBase {

    public int quantity;
    public int size;
    public String crust;
    public List<String> toppings = new ArrayList<String>(10);

    @Override
    public String toString(String br) {
        String res = quantity + " x " + size + " " + crust + ": ";
        String sep = "";
        for (String topping : toppings) {
            res += sep + topping;
            sep = ", ";
        }
        return res;
    }
}
