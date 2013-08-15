begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Thrown by {@link EarlyTerminatingCollector} when the maximum to abort  * the scoring / collection process early, when the specified maximum number  * of documents were collected.  */
end_comment
begin_class
DECL|class|EarlyTerminatingCollectorException
specifier|public
class|class
name|EarlyTerminatingCollectorException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5939241340763428118L
decl_stmt|;
DECL|field|numberScanned
specifier|private
name|int
name|numberScanned
decl_stmt|;
DECL|field|numberCollected
specifier|private
name|int
name|numberCollected
decl_stmt|;
DECL|method|EarlyTerminatingCollectorException
specifier|public
name|EarlyTerminatingCollectorException
parameter_list|(
name|int
name|numberCollected
parameter_list|,
name|int
name|numberScanned
parameter_list|)
block|{
assert|assert
name|numberCollected
operator|<=
name|numberScanned
operator|:
name|numberCollected
operator|+
literal|"<="
operator|+
name|numberScanned
assert|;
assert|assert
literal|0
operator|<
name|numberCollected
assert|;
assert|assert
literal|0
operator|<
name|numberScanned
assert|;
name|this
operator|.
name|numberCollected
operator|=
name|numberCollected
expr_stmt|;
name|this
operator|.
name|numberScanned
operator|=
name|numberScanned
expr_stmt|;
block|}
comment|/**    * The total number of documents in the index that were "scanned" by     * the index when collecting the {@see #getNumberCollected()} documents     * that triggered this exception.    *<p>    * This number represents the sum of:    *</p>    *<ul>    *<li>The total number of documents in all AtomicReaders    *      that were fully exhausted during collection    *</li>    *<li>The id of the last doc collected in the last AtomicReader    *      consulted during collection.    *</li>    *</ul>    **/
DECL|method|getNumberScanned
specifier|public
name|int
name|getNumberScanned
parameter_list|()
block|{
return|return
name|numberScanned
return|;
block|}
comment|/**    * The number of documents collected that resulted in early termination    */
DECL|method|getNumberCollected
specifier|public
name|int
name|getNumberCollected
parameter_list|()
block|{
return|return
name|numberCollected
return|;
block|}
block|}
end_class
end_unit
