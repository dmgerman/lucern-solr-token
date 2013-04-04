begin_unit
begin_package
DECL|package|org.apache.lucene.facet.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
operator|.
name|ChildrenIterator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import
begin_comment
comment|/** Prints how many ords are under each dimension. */
end_comment
begin_comment
comment|// java -cp ../build/core/classes/java:../build/facet/classes/java org.apache.lucene.facet.util.PrintTaxonomyStats -printTree /s2/scratch/indices/wikibig.trunk.noparents.facets.Lucene41.nd1M/facets
end_comment
begin_class
DECL|class|PrintTaxonomyStats
specifier|public
class|class
name|PrintTaxonomyStats
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|printTree
init|=
literal|false
decl_stmt|;
name|String
name|path
init|=
literal|null
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-printTree"
argument_list|)
condition|)
block|{
name|printTree
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|!=
operator|(
name|printTree
condition|?
literal|2
else|:
literal|1
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nUsage: java -classpath ... org.apache.lucene.facet.util.PrintTaxonomyStats [-printTree] /path/to/taxononmy/index\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|r
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|printStats
argument_list|(
name|r
argument_list|,
name|System
operator|.
name|out
argument_list|,
name|printTree
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|printStats
specifier|public
specifier|static
name|void
name|printStats
parameter_list|(
name|TaxonomyReader
name|r
parameter_list|,
name|PrintStream
name|out
parameter_list|,
name|boolean
name|printTree
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|println
argument_list|(
name|r
operator|.
name|getSize
argument_list|()
operator|+
literal|" total categories."
argument_list|)
expr_stmt|;
name|ChildrenIterator
name|it
init|=
name|r
operator|.
name|getChildren
argument_list|(
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
argument_list|)
decl_stmt|;
name|int
name|child
decl_stmt|;
while|while
condition|(
operator|(
name|child
operator|=
name|it
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|ChildrenIterator
name|chilrenIt
init|=
name|r
operator|.
name|getChildren
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|int
name|numImmediateChildren
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|chilrenIt
operator|.
name|next
argument_list|()
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|numImmediateChildren
operator|++
expr_stmt|;
block|}
name|CategoryPath
name|cp
init|=
name|r
operator|.
name|getPath
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"/"
operator|+
name|cp
operator|+
literal|": "
operator|+
name|numImmediateChildren
operator|+
literal|" immediate children; "
operator|+
operator|(
literal|1
operator|+
name|countAllChildren
argument_list|(
name|r
argument_list|,
name|child
argument_list|)
operator|)
operator|+
literal|" total categories"
argument_list|)
expr_stmt|;
if|if
condition|(
name|printTree
condition|)
block|{
name|printAllChildren
argument_list|(
name|out
argument_list|,
name|r
argument_list|,
name|child
argument_list|,
literal|"  "
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|countAllChildren
specifier|private
specifier|static
name|int
name|countAllChildren
parameter_list|(
name|TaxonomyReader
name|r
parameter_list|,
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|ChildrenIterator
name|it
init|=
name|r
operator|.
name|getChildren
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|int
name|child
decl_stmt|;
while|while
condition|(
operator|(
name|child
operator|=
name|it
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|count
operator|+=
literal|1
operator|+
name|countAllChildren
argument_list|(
name|r
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|printAllChildren
specifier|private
specifier|static
name|void
name|printAllChildren
parameter_list|(
name|PrintStream
name|out
parameter_list|,
name|TaxonomyReader
name|r
parameter_list|,
name|int
name|ord
parameter_list|,
name|String
name|indent
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|IOException
block|{
name|ChildrenIterator
name|it
init|=
name|r
operator|.
name|getChildren
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|int
name|child
decl_stmt|;
while|while
condition|(
operator|(
name|child
operator|=
name|it
operator|.
name|next
argument_list|()
operator|)
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|indent
operator|+
literal|"/"
operator|+
name|r
operator|.
name|getPath
argument_list|(
name|child
argument_list|)
operator|.
name|components
index|[
name|depth
index|]
argument_list|)
expr_stmt|;
name|printAllChildren
argument_list|(
name|out
argument_list|,
name|r
argument_list|,
name|child
argument_list|,
name|indent
operator|+
literal|"  "
argument_list|,
name|depth
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
