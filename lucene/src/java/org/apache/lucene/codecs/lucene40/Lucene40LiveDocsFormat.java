begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|codecs
operator|.
name|LiveDocsFormat
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
name|IndexFileNames
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
name|SegmentInfo
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
name|IOContext
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
name|Bits
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
name|IOUtils
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
name|MutableBits
import|;
end_import
begin_class
DECL|class|Lucene40LiveDocsFormat
specifier|public
class|class
name|Lucene40LiveDocsFormat
extends|extends
name|LiveDocsFormat
block|{
comment|/** Extension of deletes */
DECL|field|DELETES_EXTENSION
specifier|static
specifier|final
name|String
name|DELETES_EXTENSION
init|=
literal|"del"
decl_stmt|;
annotation|@
name|Override
DECL|method|newLiveDocs
specifier|public
name|MutableBits
name|newLiveDocs
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|BitVector
name|bitVector
init|=
operator|new
name|BitVector
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|bitVector
operator|.
name|invertAll
argument_list|()
expr_stmt|;
return|return
name|bitVector
return|;
block|}
annotation|@
name|Override
DECL|method|readLiveDocs
specifier|public
name|Bits
name|readLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filename
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|BitVector
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeLiveDocs
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit: this api is ugly...
name|String
name|filename
init|=
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
decl_stmt|;
comment|// nocommit: test if we really need this
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|(
operator|(
name|BitVector
operator|)
name|bits
operator|)
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|filename
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|separateFiles
specifier|public
name|void
name|separateFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|fileNameFromGeneration
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|DELETES_EXTENSION
argument_list|,
name|info
operator|.
name|getDelGen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
