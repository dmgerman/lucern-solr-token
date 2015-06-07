begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceConfigurationError
import|;
end_import
begin_comment
comment|/**  * Helper class for loading named SPIs from classpath (e.g. Codec, PostingsFormat).  * @lucene.internal  */
end_comment
begin_class
DECL|class|NamedSPILoader
specifier|public
specifier|final
class|class
name|NamedSPILoader
parameter_list|<
name|S
extends|extends
name|NamedSPILoader
operator|.
name|NamedSPI
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|S
argument_list|>
block|{
DECL|field|services
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|services
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
DECL|field|clazz
specifier|private
specifier|final
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
decl_stmt|;
DECL|method|NamedSPILoader
specifier|public
name|NamedSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
argument_list|(
name|clazz
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|NamedSPILoader
specifier|public
name|NamedSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|,
name|ClassLoader
name|classloader
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
comment|// if clazz' classloader is not a parent of the given one, we scan clazz's classloader, too:
specifier|final
name|ClassLoader
name|clazzClassloader
init|=
name|clazz
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|clazzClassloader
operator|!=
literal|null
operator|&&
operator|!
name|SPIClassIterator
operator|.
name|isParentClassLoader
argument_list|(
name|clazzClassloader
argument_list|,
name|classloader
argument_list|)
condition|)
block|{
name|reload
argument_list|(
name|clazzClassloader
argument_list|)
expr_stmt|;
block|}
name|reload
argument_list|(
name|classloader
argument_list|)
expr_stmt|;
block|}
comment|/**     * Reloads the internal SPI list from the given {@link ClassLoader}.    * Changes to the service list are visible after the method ends, all    * iterators ({@link #iterator()},...) stay consistent.     *     *<p><b>NOTE:</b> Only new service providers are added, existing ones are    * never removed or replaced.    *     *<p><em>This method is expensive and should only be called for discovery    * of new service providers on the given classpath/classloader!</em>    */
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|ClassLoader
name|classloader
parameter_list|)
block|{
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|services
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|services
argument_list|)
decl_stmt|;
specifier|final
name|SPIClassIterator
argument_list|<
name|S
argument_list|>
name|loader
init|=
name|SPIClassIterator
operator|.
name|get
argument_list|(
name|clazz
argument_list|,
name|classloader
argument_list|)
decl_stmt|;
while|while
condition|(
name|loader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|c
init|=
name|loader
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|S
name|service
init|=
name|c
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|service
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// only add the first one for each name, later services will be ignored
comment|// this allows to place services before others in classpath to make
comment|// them used instead of others
if|if
condition|(
operator|!
name|services
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|checkServiceName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|services
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceConfigurationError
argument_list|(
literal|"Cannot instantiate SPI class: "
operator|+
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|services
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates that a service name meets the requirements of {@link NamedSPI}    */
DECL|method|checkServiceName
specifier|public
specifier|static
name|void
name|checkServiceName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// based on harmony charset.java
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|>=
literal|128
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal service name: '"
operator|+
name|name
operator|+
literal|"' is too long (must be< 128 chars)."
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|len
init|=
name|name
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal service name: '"
operator|+
name|name
operator|+
literal|"' must be simple ascii alphanumeric."
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Checks whether a character is a letter or digit (ascii) which are defined in the spec.    */
DECL|method|isLetterOrDigit
specifier|private
specifier|static
name|boolean
name|isLetterOrDigit
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
literal|'a'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'z'
operator|)
operator|||
operator|(
literal|'A'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'Z'
operator|)
operator|||
operator|(
literal|'0'
operator|<=
name|c
operator|&&
name|c
operator|<=
literal|'9'
operator|)
return|;
block|}
DECL|method|lookup
specifier|public
name|S
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|S
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
return|return
name|service
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"An SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' does not exist."
operator|+
literal|"  You need to add the corresponding JAR file supporting this SPI to your classpath."
operator|+
literal|"  The current classpath supports the following names: "
operator|+
name|availableServices
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|availableServices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|availableServices
parameter_list|()
block|{
return|return
name|services
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|services
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Interface to support {@link NamedSPILoader#lookup(String)} by name.    *<p>    * Names must be all ascii alphanumeric, and less than 128 characters in length.    */
DECL|interface|NamedSPI
specifier|public
specifier|static
interface|interface
name|NamedSPI
block|{
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
block|}
block|}
end_class
end_unit
