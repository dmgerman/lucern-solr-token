begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|analysis
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
name|Field
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
name|Term
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
name|IndexReader
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
name|QueryWrapperFilter
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
name|util
operator|.
name|NumericUtils
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|TestDistance
specifier|public
class|class
name|TestDistance
extends|extends
name|TestCase
block|{
DECL|field|directory
specifier|private
name|RAMDirectory
name|directory
decl_stmt|;
comment|// reston va
DECL|field|lat
specifier|private
name|double
name|lat
init|=
literal|38.969398
decl_stmt|;
DECL|field|lng
specifier|private
name|double
name|lng
init|=
operator|-
literal|77.386398
decl_stmt|;
DECL|field|latField
specifier|private
name|String
name|latField
init|=
literal|"lat"
decl_stmt|;
DECL|field|lngField
specifier|private
name|String
name|lngField
init|=
literal|"lng"
decl_stmt|;
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addData
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addPoint
specifier|private
name|void
name|addPoint
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lng
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// convert the lat / long to lucene fields
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|latField
argument_list|,
name|NumericUtils
operator|.
name|doubleToPrefixCoded
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|lngField
argument_list|,
name|NumericUtils
operator|.
name|doubleToPrefixCoded
argument_list|(
name|lng
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a default meta field to make searching all documents easy
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"metafile"
argument_list|,
literal|"doc"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|addData
specifier|private
name|void
name|addData
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"McCormick&amp; Schmick's Seafood Restaurant"
argument_list|,
literal|38.9579000
argument_list|,
operator|-
literal|77.3572000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Jimmy's Old Town Tavern"
argument_list|,
literal|38.9690000
argument_list|,
operator|-
literal|77.3862000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Ned Devine's"
argument_list|,
literal|38.9510000
argument_list|,
operator|-
literal|77.4107000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Old Brogue Irish Pub"
argument_list|,
literal|38.9955000
argument_list|,
operator|-
literal|77.2884000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Alf Laylah Wa Laylah"
argument_list|,
literal|38.8956000
argument_list|,
operator|-
literal|77.4258000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Sully's Restaurant&amp; Supper"
argument_list|,
literal|38.9003000
argument_list|,
operator|-
literal|77.4467000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"TGIFriday"
argument_list|,
literal|38.8725000
argument_list|,
operator|-
literal|77.3829000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Potomac Swing Dance Club"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2639000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"White Tiger Restaurant"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2638000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Jammin' Java"
argument_list|,
literal|38.9039000
argument_list|,
operator|-
literal|77.2622000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Potomac Swing Dance Club"
argument_list|,
literal|38.9027000
argument_list|,
operator|-
literal|77.2639000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"WiseAcres Comedy Club"
argument_list|,
literal|38.9248000
argument_list|,
operator|-
literal|77.2344000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Glen Echo Spanish Ballroom"
argument_list|,
literal|38.9691000
argument_list|,
operator|-
literal|77.1400000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Whitlow's on Wilson"
argument_list|,
literal|38.8889000
argument_list|,
operator|-
literal|77.0926000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Iota Club and Cafe"
argument_list|,
literal|38.8890000
argument_list|,
operator|-
literal|77.0923000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"Hilton Washington Embassy Row"
argument_list|,
literal|38.9103000
argument_list|,
operator|-
literal|77.0451000
argument_list|)
expr_stmt|;
name|addPoint
argument_list|(
name|writer
argument_list|,
literal|"HorseFeathers, Bar& Grill"
argument_list|,
literal|39.01220000000001
argument_list|,
operator|-
literal|77.3942
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|testLatLongFilterOnDeletedDocs
specifier|public
name|void
name|testLatLongFilterOnDeletedDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"Potomac"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|LatLongDistanceFilter
name|f
init|=
operator|new
name|LatLongDistanceFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
argument_list|,
name|lat
argument_list|,
name|lng
argument_list|,
literal|1.0
argument_list|,
name|latField
argument_list|,
name|lngField
argument_list|)
decl_stmt|;
name|IndexReader
index|[]
name|readers
init|=
name|r
operator|.
name|getSequentialSubReaders
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|f
operator|.
name|getDocIdSet
argument_list|(
name|readers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMiles
specifier|public
name|void
name|testMiles
parameter_list|()
block|{
name|double
name|LLM
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getLLMDistance
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
literal|39.012200001
argument_list|,
operator|-
literal|77.3942
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|LLM
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-->"
operator|+
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|,
literal|39.0122
argument_list|,
operator|-
literal|77.3942
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMiles2
specifier|public
name|void
name|testMiles2
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test Miles 2"
argument_list|)
expr_stmt|;
name|double
name|LLM
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getLLMDistance
argument_list|(
literal|44.30073
argument_list|,
operator|-
literal|78.32131
argument_list|,
literal|43.687267
argument_list|,
operator|-
literal|79.39842
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|LLM
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-->"
operator|+
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getDistanceMi
argument_list|(
literal|44.30073
argument_list|,
operator|-
literal|78.32131
argument_list|,
literal|43.687267
argument_list|,
operator|-
literal|79.39842
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//  public void testDistanceQueryCacheable() throws IOException {
comment|//
comment|//    // create two of the same distance queries
comment|//    double miles = 6.0;
comment|//    DistanceQuery dq1 = new DistanceQuery(lat, lng, miles, latField, lngField, true);
comment|//    DistanceQuery dq2 = new DistanceQuery(lat, lng, miles, latField, lngField, true);
comment|//
comment|//    /* ensure that they hash to the same code, which will cause a cache hit in solr */
comment|//    System.out.println("hash differences?");
comment|//    assertEquals(dq1.getQuery().hashCode(), dq2.getQuery().hashCode());
comment|//
comment|//    /* ensure that changing the radius makes a different hash code, creating a cache miss in solr */
comment|//    DistanceQuery widerQuery = new DistanceQuery(lat, lng, miles + 5.0, latField, lngField, false);
comment|//    assertTrue(dq1.getQuery().hashCode() != widerQuery.getQuery().hashCode());
comment|//  }
block|}
end_class
end_unit
