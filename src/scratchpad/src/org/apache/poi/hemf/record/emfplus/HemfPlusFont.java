/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.hemf.record.emfplus;

import java.io.IOException;

import org.apache.poi.hemf.record.emfplus.HemfPlusDraw.EmfPlusUnitType;
import org.apache.poi.hemf.record.emfplus.HemfPlusHeader.EmfPlusGraphicsVersion;
import org.apache.poi.hemf.record.emfplus.HemfPlusObject.EmfPlusObjectData;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianConsts;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.StringUtil;

public class HemfPlusFont {


    public static class EmfPlusFont implements EmfPlusObjectData {
        /**
         * If set, the font typeface MUST be rendered with a heavier weight or thickness.
         * If clear, the font typeface MUST be rendered with a normal thickness.
         */
        private static final BitField BOLD = BitFieldFactory.getInstance(0x00000001);

        /**
         * If set, the font typeface MUST be rendered with the vertical stems of the characters at an increased angle
         * or slant relative to the baseline.
         *
         * If clear, the font typeface MUST be rendered with the vertical stems of the characters at a normal angle.
         */
        private static final BitField ITALIC = BitFieldFactory.getInstance(0x00000002);

        /**
         * If set, the font typeface MUST be rendered with a line underneath the baseline of the characters.
         * If clear, the font typeface MUST be rendered without a line underneath the baseline.
         */
        private static final BitField UNDERLINE = BitFieldFactory.getInstance(0x00000004);

        /**
         * If set, the font typeface MUST be rendered with a line parallel to the baseline drawn through the middle of
         * the characters.
         * If clear, the font typeface MUST be rendered without a line through the characters.
         */
        private static final BitField STRIKEOUT = BitFieldFactory.getInstance(0x00000008);


        private final EmfPlusGraphicsVersion version = new EmfPlusGraphicsVersion();
        private double emSize;
        private EmfPlusUnitType sizeUnit;
        private int styleFlags;
        private String family;

        @Override
        public long init(LittleEndianInputStream leis, long dataSize, HemfPlusObject.EmfPlusObjectType objectType, int flags) throws IOException {
            // An EmfPlusGraphicsVersion object that specifies the version of operating system graphics that was used
            // to create this object.
            long size = version.init(leis);

            // A 32-bit floating-point value that specifies the em size of the font in units specified by the SizeUnit field.
            emSize = leis.readFloat();

            // A 32-bit unsigned integer that specifies the units used for the EmSize field. These are typically the
            // units that were employed when designing the font. The value MUST be in the UnitType enumeration
            sizeUnit = EmfPlusUnitType.valueOf(leis.readInt());

            // A 32-bit signed integer that specifies attributes of the character glyphs that affect the appearance of
            // the font, such as bold and italic. This value MUST be composed of FontStyle flags
            styleFlags = leis.readInt();

            // A 32-bit unsigned integer that is reserved and MUST be ignored.
            leis.skipFully(LittleEndianConsts.INT_SIZE);

            // A 32-bit unsigned integer that specifies the number of characters in the FamilyName field.
            int len = leis.readInt();
            size += 5*LittleEndianConsts.INT_SIZE;

            // A string of Length Unicode characters that contains the name of the font family.
            family = StringUtil.readUnicodeLE(leis, len);
            size += len*LittleEndianConsts.SHORT_SIZE;

            return size;
        }
    }
}