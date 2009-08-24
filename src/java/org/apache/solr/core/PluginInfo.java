begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|DOMUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * An Object which represents a Plugin of any type   * @version $Id$  */
end_comment
begin_class
DECL|class|PluginInfo
specifier|public
class|class
name|PluginInfo
block|{
DECL|field|startup
DECL|field|name
DECL|field|className
DECL|field|type
specifier|public
specifier|final
name|String
name|startup
decl_stmt|,
name|name
decl_stmt|,
name|className
decl_stmt|,
name|type
decl_stmt|;
DECL|field|isDefault
specifier|public
specifier|final
name|boolean
name|isDefault
decl_stmt|;
DECL|field|initArgs
specifier|public
specifier|final
name|NamedList
name|initArgs
decl_stmt|;
DECL|field|attributes
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|method|PluginInfo
specifier|public
name|PluginInfo
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|startup
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|boolean
name|isdefault
parameter_list|,
name|NamedList
name|initArgs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|otherAttrs
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|startup
operator|=
name|startup
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|isDefault
operator|=
name|isdefault
expr_stmt|;
name|this
operator|.
name|initArgs
operator|=
name|initArgs
expr_stmt|;
name|attributes
operator|=
name|otherAttrs
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|emptyMap
argument_list|()
operator|:
name|otherAttrs
expr_stmt|;
block|}
DECL|method|PluginInfo
specifier|public
name|PluginInfo
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|err
parameter_list|,
name|boolean
name|requireName
parameter_list|)
block|{
name|type
operator|=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
name|name
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"name"
argument_list|,
name|requireName
condition|?
name|err
else|:
literal|null
argument_list|)
expr_stmt|;
name|className
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"class"
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|isDefault
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"default"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|startup
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"startup"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|initArgs
operator|=
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|nnm
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nnm
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|nnm
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attributes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"name = "
operator|+
name|name
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"class = "
operator|+
name|className
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDefault
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"default = "
operator|+
name|isDefault
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|startup
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"startup = "
operator|+
name|startup
operator|+
literal|","
argument_list|)
expr_stmt|;
if|if
condition|(
name|initArgs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"args = "
operator|+
name|initArgs
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
