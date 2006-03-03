begin_unit
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  *<code>DocSet</code> represents an unordered set of Lucene Document Ids.  *<p>  * WARNING: Any DocSet returned from SolrIndexSearcher should<b>not</b> be modified as it may have been retrieved from  * a cache and could be shared.  * @author yonik  * @version $Id$  * @since solr 0.9  */
end_comment
begin_interface
DECL|interface|DocSet
specifier|public
interface|interface
name|DocSet
comment|/* extends Collection<Integer> */
block|{
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
DECL|method|addUnique
specifier|public
name|void
name|addUnique
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
comment|/**    * @return The number of document ids in the set.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    *    * @param docid    * @return    * true if the docid is in the set    */
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|docid
parameter_list|)
function_decl|;
comment|/**    *    * @return an interator that may be used to iterate over all of the documents in the set.    */
DECL|method|iterator
specifier|public
name|DocIterator
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Returns a BitSet view of the DocSet.  Any changes to this BitSet<b>may</b>    * be reflected in the DocSet, hence if the DocSet is shared or was returned from    * a SolrIndexSearcher method, it's not safe to modify the BitSet.    *    * @return    * A BitSet with the bit number of every docid set in the set.    */
annotation|@
name|Deprecated
DECL|method|getBits
specifier|public
name|BitSet
name|getBits
parameter_list|()
function_decl|;
comment|/**    * Returns the approximate amount of memory taken by this DocSet.    * This is only an approximation and doesn't take into account java object overhead.    *    * @return    * the approximate memory consumption in bytes    */
DECL|method|memSize
specifier|public
name|long
name|memSize
parameter_list|()
function_decl|;
comment|/**    * Returns the intersection of this set with another set.  Neither set is modified - a new DocSet is    * created and returned.    * @param other    * @return a DocSet representing the intersection    */
DECL|method|intersection
specifier|public
name|DocSet
name|intersection
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents of the intersection of this set with another set.    * May be more efficient than actually creating the intersection and then getting it's size.    */
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the union of this set with another set.  Neither set is modified - a new DocSet is    * created and returned.    * @param other    * @return a DocSet representing the union    */
DECL|method|union
specifier|public
name|DocSet
name|union
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
comment|/**    * Returns the number of documents of the union of this set with another set.    * May be more efficient than actually creating the union and then getting it's size.    */
DECL|method|unionSize
specifier|public
name|int
name|unionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
function_decl|;
block|}
end_interface
begin_class
DECL|class|DocSetBase
specifier|abstract
class|class
name|DocSetBase
implements|implements
name|DocSet
block|{
comment|// Not implemented efficiently... for testing purposes only
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|DocSet
operator|)
condition|)
return|return
literal|false
return|;
name|DocSet
name|other
init|=
operator|(
name|DocSet
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|size
argument_list|()
operator|!=
name|other
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|instanceof
name|DocList
operator|&&
name|other
operator|instanceof
name|DocList
condition|)
block|{
comment|// compare ordering
name|DocIterator
name|i1
init|=
name|this
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIterator
name|i2
init|=
name|this
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|&&
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|i1
operator|.
name|nextDoc
argument_list|()
operator|!=
name|i2
operator|.
name|nextDoc
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
comment|// don't compare matches
block|}
comment|// if (this.size() != other.size()) return false;
return|return
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|500
argument_list|,
literal|"Unsupported Operation"
argument_list|)
throw|;
block|}
DECL|method|addUnique
specifier|public
name|void
name|addUnique
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|500
argument_list|,
literal|"Unsupported Operation"
argument_list|)
throw|;
block|}
comment|// Only the inefficient base implementation.  DocSets based on
comment|// BitSets will return the actual BitSet without making a copy.
DECL|method|getBits
specifier|public
name|BitSet
name|getBits
parameter_list|()
block|{
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|DocIterator
name|iter
init|=
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
empty_stmt|;
DECL|method|intersection
specifier|public
name|DocSet
name|intersection
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
comment|// intersection is overloaded in HashDocSet to be more
comment|// efficient, so if "other" is a HashDocSet, dispatch off
comment|// of it instead.
if|if
condition|(
name|other
operator|instanceof
name|HashDocSet
condition|)
block|{
return|return
name|other
operator|.
name|intersection
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// Default... handle with bitsets.
name|BitSet
name|newbits
init|=
call|(
name|BitSet
call|)
argument_list|(
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|newbits
operator|.
name|and
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocSet
argument_list|(
name|newbits
argument_list|)
return|;
block|}
DECL|method|union
specifier|public
name|DocSet
name|union
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
name|BitSet
name|newbits
init|=
call|(
name|BitSet
call|)
argument_list|(
name|this
operator|.
name|getBits
argument_list|()
operator|.
name|clone
argument_list|()
argument_list|)
decl_stmt|;
name|newbits
operator|.
name|or
argument_list|(
name|other
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocSet
argument_list|(
name|newbits
argument_list|)
return|;
block|}
comment|// TODO: more efficient implementations
DECL|method|intersectionSize
specifier|public
name|int
name|intersectionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
return|return
name|intersection
argument_list|(
name|other
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
comment|// TODO: more efficient implementations
DECL|method|unionSize
specifier|public
name|int
name|unionSize
parameter_list|(
name|DocSet
name|other
parameter_list|)
block|{
return|return
name|union
argument_list|(
name|other
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class
end_unit
