begin_unit
comment|'#! /usr/bin/env python'
nl|'\n'
nl|'\n'
comment|'# Licensed to the Apache Software Foundation (ASF) under one or more'
nl|'\n'
comment|'# contributor license agreements.  See the NOTICE file distributed with'
nl|'\n'
comment|'# this work for additional information regarding copyright ownership.'
nl|'\n'
comment|'# The ASF licenses this file to You under the Apache License, Version 2.0'
nl|'\n'
comment|'# (the "License"); you may not use this file except in compliance with'
nl|'\n'
comment|'# the License.  You may obtain a copy of the License at'
nl|'\n'
comment|'#'
nl|'\n'
comment|'#     http://www.apache.org/licenses/LICENSE-2.0'
nl|'\n'
comment|'#'
nl|'\n'
comment|'# Unless required by applicable law or agreed to in writing, software'
nl|'\n'
comment|'# distributed under the License is distributed on an "AS IS" BASIS,'
nl|'\n'
comment|'# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.'
nl|'\n'
comment|'# See the License for the specific language governing permissions and'
nl|'\n'
comment|'# limitations under the License.'
nl|'\n'
nl|'\n'
name|'from'
name|'fractions'
name|'import'
name|'gcd'
newline|'\n'
nl|'\n'
string|'"""Code generation for bulk operations"""'
newline|'\n'
nl|'\n'
DECL|variable|MAX_SPECIALIZED_BITS_PER_VALUE
name|'MAX_SPECIALIZED_BITS_PER_VALUE'
op|'='
number|'24'
op|';'
newline|'\n'
DECL|variable|PACKED_64_SINGLE_BLOCK_BPV
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|'='
op|'['
number|'1'
op|','
number|'2'
op|','
number|'3'
op|','
number|'4'
op|','
number|'5'
op|','
number|'6'
op|','
number|'7'
op|','
number|'8'
op|','
number|'9'
op|','
number|'10'
op|','
number|'12'
op|','
number|'16'
op|','
number|'21'
op|','
number|'32'
op|']'
newline|'\n'
DECL|variable|OUTPUT_FILE
name|'OUTPUT_FILE'
op|'='
string|'"BulkOperation.java"'
newline|'\n'
name|'HEADER'
op|'='
string|'"""// This file has been automatically generated, DO NOT EDIT\n\npackage org.apache.lucene.util.packed;\n\n/*\n * Licensed to the Apache Software Foundation (ASF) under one or more\n * contributor license agreements.  See the NOTICE file distributed with\n * this work for additional information regarding copyright ownership.\n * The ASF licenses this file to You under the Apache License, Version 2.0\n * (the "License"); you may not use this file except in compliance with\n * the License.  You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an "AS IS" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n\n"""'
newline|'\n'
nl|'\n'
name|'FOOTER'
op|'='
string|'"""\n  protected int writeLong(long block, byte[] blocks, int blocksOffset) {\n    for (int j = 1; j <= 8; ++j) {\n      blocks[blocksOffset++] = (byte) (block >>> (64 - (j << 3)));\n    }\n    return blocksOffset;\n  }\n\n  /**\n   * For every number of bits per value, there is a minimum number of\n   * blocks (b) / values (v) you need to write in order to reach the next block\n   * boundary:\n   *  - 16 bits per value -&gt; b=2, v=1\n   *  - 24 bits per value -&gt; b=3, v=1\n   *  - 50 bits per value -&gt; b=25, v=4\n   *  - 63 bits per value -&gt; b=63, v=8\n   *  - ...\n   *\n   * A bulk read consists in copying <code>iterations*v</code> values that are\n   * contained in <code>iterations*b</code> blocks into a <code>long[]</code>\n   * (higher values of <code>iterations</code> are likely to yield a better\n   * throughput): this requires n * (b + 8v) bytes of memory.\n   *\n   * This method computes <code>iterations</code> as\n   * <code>ramBudget / (b + 8v)</code> (since a long is 8 bytes).\n   */\n  public final int computeIterations(int valueCount, int ramBudget) {\n    final int iterations = ramBudget / (byteBlockCount() + 8 * byteValueCount());\n    if (iterations == 0) {\n      // at least 1\n      return 1;\n    } else if ((iterations - 1) * byteValueCount() >= valueCount) {\n      // don\'t allocate for more than the size of the reader\n      return (int) Math.ceil((double) valueCount / byteValueCount());\n    } else {\n      return iterations;\n    }\n  }\n}\n"""'
newline|'\n'
nl|'\n'
DECL|function|is_power_of_two
name|'def'
name|'is_power_of_two'
op|'('
name|'n'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'return'
name|'n'
op|'&'
op|'('
name|'n'
op|'-'
number|'1'
op|')'
op|'=='
number|'0'
newline|'\n'
nl|'\n'
DECL|function|casts
dedent|''
name|'def'
name|'casts'
op|'('
name|'typ'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'cast_start'
op|'='
string|'"(%s) ("'
op|'%'
name|'typ'
newline|'\n'
name|'cast_end'
op|'='
string|'")"'
newline|'\n'
name|'if'
name|'typ'
op|'=='
string|'"long"'
op|':'
newline|'\n'
indent|'    '
name|'cast_start'
op|'='
string|'""'
newline|'\n'
name|'cast_end'
op|'='
string|'""'
newline|'\n'
dedent|''
name|'return'
name|'cast_start'
op|','
name|'cast_end'
newline|'\n'
nl|'\n'
DECL|function|hexNoLSuffix
dedent|''
name|'def'
name|'hexNoLSuffix'
op|'('
name|'n'
op|')'
op|':'
newline|'\n'
comment|'# On 32 bit Python values > (1 << 31)-1 will have L appended by hex function:'
nl|'\n'
indent|'  '
name|'s'
op|'='
name|'hex'
op|'('
name|'n'
op|')'
newline|'\n'
name|'if'
name|'s'
op|'.'
name|'endswith'
op|'('
string|"'L'"
op|')'
op|':'
newline|'\n'
indent|'    '
name|'s'
op|'='
name|'s'
op|'['
op|':'
op|'-'
number|'1'
op|']'
newline|'\n'
dedent|''
name|'return'
name|'s'
newline|'\n'
nl|'\n'
DECL|function|masks
dedent|''
name|'def'
name|'masks'
op|'('
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'bits'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'""'
op|','
string|'""'
newline|'\n'
dedent|''
name|'return'
string|'"("'
op|','
string|'" & %sL)"'
op|'%'
op|'('
name|'hexNoLSuffix'
op|'('
op|'('
number|'1'
op|'<<'
name|'bits'
op|')'
op|'-'
number|'1'
op|')'
op|')'
newline|'\n'
nl|'\n'
DECL|function|get_type
dedent|''
name|'def'
name|'get_type'
op|'('
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'if'
name|'bits'
op|'=='
number|'8'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"byte"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'16'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"short"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'32'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"int"'
newline|'\n'
dedent|''
name|'elif'
name|'bits'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'return'
string|'"long"'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'assert'
name|'False'
newline|'\n'
nl|'\n'
DECL|function|block_value_count
dedent|''
dedent|''
name|'def'
name|'block_value_count'
op|'('
name|'bpv'
op|','
name|'bits'
op|'='
number|'64'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'blocks'
op|'='
name|'bpv'
newline|'\n'
name|'values'
op|'='
name|'blocks'
op|'*'
name|'bits'
op|'/'
name|'bpv'
newline|'\n'
name|'while'
name|'blocks'
op|'%'
number|'2'
op|'=='
number|'0'
name|'and'
name|'values'
op|'%'
number|'2'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'    '
name|'blocks'
op|'/='
number|'2'
newline|'\n'
name|'values'
op|'/='
number|'2'
newline|'\n'
dedent|''
name|'assert'
name|'values'
op|'*'
name|'bpv'
op|'=='
name|'bits'
op|'*'
name|'blocks'
op|','
string|'"%d values, %d blocks, %d bits per value"'
op|'%'
op|'('
name|'values'
op|','
name|'blocks'
op|','
name|'bpv'
op|')'
newline|'\n'
name|'return'
op|'('
name|'blocks'
op|','
name|'values'
op|')'
newline|'\n'
nl|'\n'
DECL|function|packed64
dedent|''
name|'def'
name|'packed64'
op|'('
name|'bpv'
op|','
name|'f'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'mask'
op|'='
op|'('
number|'1'
op|'<<'
name|'bpv'
op|')'
op|'-'
number|'1'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  public BulkOperationPacked%d() {\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    super(%d);\\n"'
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'if'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"""    @Override\n    public void decode(long[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {\n      System.arraycopy(blocks, blocksOffset, values, valuesOffset, valueCount() * iterations);\n    }\n\n    @Override\n    public void decode(long[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {\n      throw new UnsupportedOperationException();\n    }\n\n    @Override\n    public void decode(byte[] blocks, int blocksOffset, int[] values, int valuesOffset, int iterations) {\n      throw new UnsupportedOperationException();\n    }\n\n    @Override\n    public void decode(byte[] blocks, int blocksOffset, long[] values, int valuesOffset, int iterations) {\n      LongBuffer.wrap(values, valuesOffset, iterations * valueCount()).put(ByteBuffer.wrap(blocks, blocksOffset, 8 * iterations * blockCount()).asLongBuffer());\n    }\n"""'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'p64_decode'
op|'('
name|'bpv'
op|','
name|'f'
op|','
number|'32'
op|')'
newline|'\n'
name|'p64_decode'
op|'('
name|'bpv'
op|','
name|'f'
op|','
number|'64'
op|')'
newline|'\n'
nl|'\n'
DECL|function|p64_decode
dedent|''
dedent|''
name|'def'
name|'p64_decode'
op|'('
name|'bpv'
op|','
name|'f'
op|','
name|'bits'
op|')'
op|':'
newline|'\n'
indent|'  '
name|'blocks'
op|','
name|'values'
op|'='
name|'block_value_count'
op|'('
name|'bpv'
op|')'
newline|'\n'
name|'typ'
op|'='
name|'get_type'
op|'('
name|'bits'
op|')'
newline|'\n'
name|'cast_start'
op|','
name|'cast_end'
op|'='
name|'casts'
op|'('
name|'typ'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  @Override\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  public void decode(long[] blocks, int blocksOffset, %s[] values, int valuesOffset, int iterations) {\\n"'
op|'%'
name|'typ'
op|')'
newline|'\n'
name|'if'
name|'bits'
op|'<'
name|'bpv'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    throw new UnsupportedOperationException();\\n"'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'mask'
op|'='
op|'('
number|'1'
op|'<<'
name|'bpv'
op|')'
op|'-'
number|'1'
newline|'\n'
nl|'\n'
name|'if'
name|'is_power_of_two'
op|'('
name|'bpv'
op|')'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final long block = blocks[blocksOffset++];\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      for (int shift = %d; shift >= 0; shift -= %d) {\\n"'
op|'%'
op|'('
number|'64'
op|'-'
name|'bpv'
op|','
name|'bpv'
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"        values[valuesOffset++] = %s(block >>> shift) & %d%s;\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      }\\n"'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
number|'0'
op|','
name|'values'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'block_offset'
op|'='
name|'i'
op|'*'
name|'bpv'
op|'/'
number|'64'
newline|'\n'
name|'bit_offset'
op|'='
op|'('
name|'i'
op|'*'
name|'bpv'
op|')'
op|'%'
number|'64'
newline|'\n'
name|'if'
name|'bit_offset'
op|'=='
number|'0'
op|':'
newline|'\n'
comment|'# start of block'
nl|'\n'
indent|'          '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final long block%d = blocks[blocksOffset++];\\n"'
op|'%'
name|'block_offset'
op|')'
op|';'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = %sblock%d >>> %d%s;\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
number|'64'
op|'-'
name|'bpv'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
comment|'# end of block'
nl|'\n'
indent|'          '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = %sblock%d & %dL%s;\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bit_offset'
op|'+'
name|'bpv'
op|'<'
number|'64'
op|':'
newline|'\n'
comment|'# middle of block'
nl|'\n'
indent|'          '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = %s(block%d >>> %d) & %dL%s;\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
number|'64'
op|'-'
name|'bit_offset'
op|'-'
name|'bpv'
op|','
name|'mask'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
comment|'# value spans across 2 blocks'
nl|'\n'
indent|'          '
name|'mask1'
op|'='
op|'('
number|'1'
op|'<<'
op|'('
number|'64'
op|'-'
name|'bit_offset'
op|')'
op|')'
op|'-'
number|'1'
newline|'\n'
name|'shift1'
op|'='
name|'bit_offset'
op|'+'
name|'bpv'
op|'-'
number|'64'
newline|'\n'
name|'shift2'
op|'='
number|'64'
op|'-'
name|'shift1'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final long block%d = blocks[blocksOffset++];\\n"'
op|'%'
op|'('
name|'block_offset'
op|'+'
number|'1'
op|')'
op|')'
op|';'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = %s((block%d & %dL) << %d) | (block%d >>> %d)%s;\\n"'
op|'%'
op|'('
name|'cast_start'
op|','
name|'block_offset'
op|','
name|'mask1'
op|','
name|'shift1'
op|','
name|'block_offset'
op|'+'
number|'1'
op|','
name|'shift2'
op|','
name|'cast_end'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'byte_blocks'
op|','
name|'byte_values'
op|'='
name|'block_value_count'
op|'('
name|'bpv'
op|','
number|'8'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  @Override\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  public void decode(byte[] blocks, int blocksOffset, %s[] values, int valuesOffset, int iterations) {\\n"'
op|'%'
name|'typ'
op|')'
newline|'\n'
name|'if'
name|'bits'
op|'<'
name|'bpv'
op|':'
newline|'\n'
indent|'    '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    throw new UnsupportedOperationException();\\n"'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'is_power_of_two'
op|'('
name|'bpv'
op|')'
name|'and'
name|'bpv'
op|'<'
number|'8'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    for (int j = 0; j < iterations; ++j) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final byte block = blocks[blocksOffset++];\\n"'
op|')'
newline|'\n'
name|'for'
name|'shift'
name|'in'
name|'xrange'
op|'('
number|'8'
op|'-'
name|'bpv'
op|','
number|'0'
op|','
op|'-'
name|'bpv'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = (block >>> %d) & %d;\\n"'
op|'%'
op|'('
name|'shift'
op|','
name|'mask'
op|')'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = block & %d;\\n"'
op|'%'
name|'mask'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'bpv'
op|'=='
number|'8'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    for (int j = 0; j < iterations; ++j) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] = blocks[blocksOffset++] & 0xFF;\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
dedent|''
name|'elif'
name|'is_power_of_two'
op|'('
name|'bpv'
op|')'
name|'and'
name|'bpv'
op|'>'
number|'8'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    for (int j = 0; j < iterations; ++j) {\\n"'
op|')'
newline|'\n'
name|'m'
op|'='
name|'bits'
op|'<='
number|'32'
name|'and'
string|'"0xFF"'
name|'or'
string|'"0xFFL"'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] ="'
op|')'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
name|'bpv'
op|'/'
number|'8'
op|'-'
number|'1'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'f'
op|'.'
name|'write'
op|'('
string|'" ((blocks[blocksOffset++] & %s) << %d) |"'
op|'%'
op|'('
name|'m'
op|','
name|'bpv'
op|'-'
number|'8'
op|')'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'" (blocks[blocksOffset++] & %s);\\n"'
op|'%'
name|'m'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|'"    for (int i = 0; i < iterations; ++i) {\\n"'
op|')'
newline|'\n'
name|'for'
name|'i'
name|'in'
name|'xrange'
op|'('
number|'0'
op|','
name|'byte_values'
op|')'
op|':'
newline|'\n'
indent|'        '
name|'byte_start'
op|'='
name|'i'
op|'*'
name|'bpv'
op|'/'
number|'8'
newline|'\n'
name|'bit_start'
op|'='
op|'('
name|'i'
op|'*'
name|'bpv'
op|')'
op|'%'
number|'8'
newline|'\n'
name|'byte_end'
op|'='
op|'('
op|'('
name|'i'
op|'+'
number|'1'
op|')'
op|'*'
name|'bpv'
op|'-'
number|'1'
op|')'
op|'/'
number|'8'
newline|'\n'
name|'bit_end'
op|'='
op|'('
op|'('
name|'i'
op|'+'
number|'1'
op|')'
op|'*'
name|'bpv'
op|'-'
number|'1'
op|')'
op|'%'
number|'8'
newline|'\n'
name|'shift'
op|'='
name|'lambda'
name|'b'
op|':'
number|'8'
op|'*'
op|'('
name|'byte_end'
op|'-'
name|'b'
op|'-'
number|'1'
op|')'
op|'+'
number|'1'
op|'+'
name|'bit_end'
newline|'\n'
name|'if'
name|'bit_start'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'          '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final %s byte%d = blocks[blocksOffset++] & 0xFF;\\n"'
op|'%'
op|'('
name|'typ'
op|','
name|'byte_start'
op|')'
op|')'
newline|'\n'
dedent|''
name|'for'
name|'b'
name|'in'
name|'xrange'
op|'('
name|'byte_start'
op|'+'
number|'1'
op|','
name|'byte_end'
op|'+'
number|'1'
op|')'
op|':'
newline|'\n'
indent|'          '
name|'f'
op|'.'
name|'write'
op|'('
string|'"      final %s byte%d = blocks[blocksOffset++] & 0xFF;\\n"'
op|'%'
op|'('
name|'typ'
op|','
name|'b'
op|')'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"      values[valuesOffset++] ="'
op|')'
newline|'\n'
name|'if'
name|'byte_start'
op|'=='
name|'byte_end'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'bit_start'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'            '
name|'if'
name|'bit_end'
op|'=='
number|'7'
op|':'
newline|'\n'
indent|'              '
name|'f'
op|'.'
name|'write'
op|'('
string|'" byte%d"'
op|'%'
name|'byte_start'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'              '
name|'f'
op|'.'
name|'write'
op|'('
string|'" byte%d >>> %d"'
op|'%'
op|'('
name|'byte_start'
op|','
number|'7'
op|'-'
name|'bit_end'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'            '
name|'if'
name|'bit_end'
op|'=='
number|'7'
op|':'
newline|'\n'
indent|'              '
name|'f'
op|'.'
name|'write'
op|'('
string|'" byte%d & %d"'
op|'%'
op|'('
name|'byte_start'
op|','
number|'2'
op|'**'
op|'('
number|'8'
op|'-'
name|'bit_start'
op|')'
op|'-'
number|'1'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'              '
name|'f'
op|'.'
name|'write'
op|'('
string|'" (byte%d >>> %d) & %d"'
op|'%'
op|'('
name|'byte_start'
op|','
number|'7'
op|'-'
name|'bit_end'
op|','
number|'2'
op|'**'
op|'('
name|'bit_end'
op|'-'
name|'bit_start'
op|'+'
number|'1'
op|')'
op|'-'
number|'1'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'          '
name|'if'
name|'bit_start'
op|'=='
number|'0'
op|':'
newline|'\n'
indent|'            '
name|'f'
op|'.'
name|'write'
op|'('
string|'" (byte%d << %d)"'
op|'%'
op|'('
name|'byte_start'
op|','
name|'shift'
op|'('
name|'byte_start'
op|')'
op|')'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'            '
name|'f'
op|'.'
name|'write'
op|'('
string|'" ((byte%d & %d) << %d)"'
op|'%'
op|'('
name|'byte_start'
op|','
number|'2'
op|'**'
op|'('
number|'8'
op|'-'
name|'bit_start'
op|')'
op|'-'
number|'1'
op|','
name|'shift'
op|'('
name|'byte_start'
op|')'
op|')'
op|')'
newline|'\n'
dedent|''
name|'for'
name|'b'
name|'in'
name|'xrange'
op|'('
name|'byte_start'
op|'+'
number|'1'
op|','
name|'byte_end'
op|')'
op|':'
newline|'\n'
indent|'            '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | (byte%d << %d)"'
op|'%'
op|'('
name|'b'
op|','
name|'shift'
op|'('
name|'b'
op|')'
op|')'
op|')'
newline|'\n'
dedent|''
name|'if'
name|'bit_end'
op|'=='
number|'7'
op|':'
newline|'\n'
indent|'            '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | byte%d"'
op|'%'
name|'byte_end'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'            '
name|'f'
op|'.'
name|'write'
op|'('
string|'" | (byte%d >>> %d)"'
op|'%'
op|'('
name|'byte_end'
op|','
number|'7'
op|'-'
name|'bit_end'
op|')'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'";\\n"'
op|')'
newline|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n\\n"'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'if'
name|'__name__'
op|'=='
string|"'__main__'"
op|':'
newline|'\n'
DECL|variable|f
indent|'  '
name|'f'
op|'='
name|'open'
op|'('
name|'OUTPUT_FILE'
op|','
string|"'w'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
name|'HEADER'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'''/**\n * Efficient sequential read/write of packed integers.\n */\\n'''"
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'abstract class BulkOperation implements PackedInts.Decoder, PackedInts.Encoder {\\n'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'  private static final BulkOperation[] packedBulkOps = new BulkOperation[] {\\n'"
op|')'
newline|'\n'
nl|'\n'
name|'for'
name|'bpv'
name|'in'
name|'xrange'
op|'('
number|'1'
op|','
number|'65'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'bpv'
op|'>'
name|'MAX_SPECIALIZED_BITS_PER_VALUE'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|"'    new BulkOperationPacked(%d),\\n'"
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'continue'
newline|'\n'
DECL|variable|f2
dedent|''
name|'f2'
op|'='
name|'open'
op|'('
string|"'BulkOperationPacked%d.java'"
op|'%'
name|'bpv'
op|','
string|"'w'"
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'write'
op|'('
name|'HEADER'
op|')'
newline|'\n'
name|'if'
name|'bpv'
op|'=='
number|'64'
op|':'
newline|'\n'
indent|'      '
name|'f2'
op|'.'
name|'write'
op|'('
string|"'import java.nio.LongBuffer;\\n'"
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'write'
op|'('
string|"'import java.nio.ByteBuffer;\\n'"
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|')'
newline|'\n'
dedent|''
name|'f2'
op|'.'
name|'write'
op|'('
string|"'''/**\n * Efficient sequential read/write of packed integers.\n */\\n'''"
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'write'
op|'('
string|"'final class BulkOperationPacked%d extends BulkOperationPacked {\\n'"
op|'%'
name|'bpv'
op|')'
newline|'\n'
name|'packed64'
op|'('
name|'bpv'
op|','
name|'f2'
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'write'
op|'('
string|"'}\\n'"
op|')'
newline|'\n'
name|'f2'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'    new BulkOperationPacked%d(),\\n'"
op|'%'
name|'bpv'
op|')'
newline|'\n'
nl|'\n'
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|"'  };\\n'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'  // NOTE: this is sparse (some entries are null):\\n'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'  private static final BulkOperation[] packedSingleBlockBulkOps = new BulkOperation[] {\\n'"
op|')'
newline|'\n'
name|'for'
name|'bpv'
name|'in'
name|'xrange'
op|'('
number|'1'
op|','
name|'max'
op|'('
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|')'
op|'+'
number|'1'
op|')'
op|':'
newline|'\n'
indent|'    '
name|'if'
name|'bpv'
name|'in'
name|'PACKED_64_SINGLE_BLOCK_BPV'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|"'    new BulkOperationPackedSingleBlock(%d),\\n'"
op|'%'
name|'bpv'
op|')'
newline|'\n'
dedent|''
name|'else'
op|':'
newline|'\n'
indent|'      '
name|'f'
op|'.'
name|'write'
op|'('
string|"'    null,\\n'"
op|')'
newline|'\n'
dedent|''
dedent|''
name|'f'
op|'.'
name|'write'
op|'('
string|"'  };\\n'"
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|"'\\n'"
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  public static BulkOperation of(PackedInts.Format format, int bitsPerValue) {\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    switch (format) {\\n"'
op|')'
newline|'\n'
nl|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    case PACKED:\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert packedBulkOps[bitsPerValue - 1] != null;\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return packedBulkOps[bitsPerValue - 1];\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    case PACKED_SINGLE_BLOCK:\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      assert packedSingleBlockBulkOps[bitsPerValue - 1] != null;\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      return packedSingleBlockBulkOps[bitsPerValue - 1];\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    default:\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"      throw new AssertionError();\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"    }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
string|'"  }\\n"'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'write'
op|'('
name|'FOOTER'
op|')'
newline|'\n'
name|'f'
op|'.'
name|'close'
op|'('
op|')'
newline|'\n'
dedent|''
endmarker|''
end_unit
