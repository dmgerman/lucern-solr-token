begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|LongPoint
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|NumericDocValuesField
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
name|DrillDownQuery
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
name|FacetResult
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
name|Facets
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
name|FacetsCollector
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
name|FacetsConfig
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
name|range
operator|.
name|LongRange
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
name|range
operator|.
name|LongRangeFacetCounts
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|search
operator|.
name|PointRangeQuery
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|search
operator|.
name|TopDocs
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
name|RAMDirectory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
begin_comment
comment|/** Shows simple usage of dynamic range faceting. */
end_comment
begin_class
DECL|class|RangeFacetsExample
specifier|public
class|class
name|RangeFacetsExample
implements|implements
name|Closeable
block|{
DECL|field|indexDir
specifier|private
specifier|final
name|Directory
name|indexDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|nowSec
specifier|private
specifier|final
name|long
name|nowSec
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|PAST_HOUR
specifier|final
name|LongRange
name|PAST_HOUR
init|=
operator|new
name|LongRange
argument_list|(
literal|"Past hour"
argument_list|,
name|nowSec
operator|-
literal|3600
argument_list|,
literal|true
argument_list|,
name|nowSec
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|PAST_SIX_HOURS
specifier|final
name|LongRange
name|PAST_SIX_HOURS
init|=
operator|new
name|LongRange
argument_list|(
literal|"Past six hours"
argument_list|,
name|nowSec
operator|-
literal|6
operator|*
literal|3600
argument_list|,
literal|true
argument_list|,
name|nowSec
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|PAST_DAY
specifier|final
name|LongRange
name|PAST_DAY
init|=
operator|new
name|LongRange
argument_list|(
literal|"Past day"
argument_list|,
name|nowSec
operator|-
literal|24
operator|*
literal|3600
argument_list|,
literal|true
argument_list|,
name|nowSec
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Empty constructor */
DECL|method|RangeFacetsExample
specifier|public
name|RangeFacetsExample
parameter_list|()
block|{}
comment|/** Build the example index. */
DECL|method|index
specifier|public
name|void
name|index
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
comment|// Add documents with a fake timestamp, 1000 sec before
comment|// "now", 2000 sec before "now", ...:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|long
name|then
init|=
name|nowSec
operator|-
name|i
operator|*
literal|1000
decl_stmt|;
comment|// Add as doc values field, so we can compute range facets:
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"timestamp"
argument_list|,
name|then
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add as numeric field so we can drill-down:
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongPoint
argument_list|(
literal|"timestamp"
argument_list|,
name|then
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// Open near-real-time searcher
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getConfig
specifier|private
name|FacetsConfig
name|getConfig
parameter_list|()
block|{
return|return
operator|new
name|FacetsConfig
argument_list|()
return|;
block|}
comment|/** User runs a query and counts facets. */
DECL|method|search
specifier|public
name|FacetResult
name|search
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Aggregates the facet counts
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
comment|// MatchAllDocsQuery is for "browsing" (counts facets
comment|// for all non-deleted docs in the index); normally
comment|// you'd use a "normal" query:
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|LongRangeFacetCounts
argument_list|(
literal|"timestamp"
argument_list|,
name|fc
argument_list|,
name|PAST_HOUR
argument_list|,
name|PAST_SIX_HOURS
argument_list|,
name|PAST_DAY
argument_list|)
decl_stmt|;
return|return
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"timestamp"
argument_list|)
return|;
block|}
comment|/** User drills down on the specified range. */
DECL|method|drillDown
specifier|public
name|TopDocs
name|drillDown
parameter_list|(
name|LongRange
name|range
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Passing no baseQuery means we drill down on all
comment|// documents ("browse only"):
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"timestamp"
argument_list|,
name|PointRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"timestamp"
argument_list|,
name|range
operator|.
name|min
argument_list|,
name|range
operator|.
name|minInclusive
argument_list|,
name|range
operator|.
name|max
argument_list|,
name|range
operator|.
name|maxInclusive
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Runs the search and drill-down examples and prints the results. */
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
name|Exception
block|{
name|RangeFacetsExample
name|example
init|=
operator|new
name|RangeFacetsExample
argument_list|()
decl_stmt|;
name|example
operator|.
name|index
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Facet counting example:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-----------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|example
operator|.
name|search
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Facet drill-down example (timestamp/Past six hours):"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"---------------------------------------------"
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|example
operator|.
name|drillDown
argument_list|(
name|example
operator|.
name|PAST_SIX_HOURS
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|totalHits
operator|+
literal|" totalHits"
argument_list|)
expr_stmt|;
name|example
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
