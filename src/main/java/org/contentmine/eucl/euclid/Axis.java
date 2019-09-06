/**
 *    Copyright 2011 Peter Murray-Rust
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.eucl.euclid;

/**
 * enums to represent 2- or 3-D axes
 * 
 * @author (C) P. Murray-Rust, 2005
 */

public class Axis {

    /** enum for x y z axes */
    public enum Axis2 {

        /**
         * x axis. value is 0 for indexing arrays.
         */
        X("x", 0),
        /**
         * y axis. value is 1 for indexing arrays.
         */
        Y("y", 1);

        /** string value */
        public final String axis;

        /** integer value */
        public final int value;

        /**
         * constructor.
         * 
         * @param axis
         *            label for the axis
         * @param value
         *            serial number (starts at 0)
         */

        private Axis2(String axis, int value) {
            this.axis = axis;
            this.value = value;
        }

        /** gets the other axis.
         * 
         * @return Axis2.X if this.equals(Axis2.Y) and vice versa
         */
		public Axis2 otherAxis() {
			return this.equals(Axis2.X) ? Axis2.Y : Axis2.X;
		}
    }

    /** 3d axes */
    public enum Axis3 {

        /**
         * x axis. value is 0 for indexing arrays.
         */
        X("x", 0),
        /**
         * y axis. value is 1 for indexing arrays.
         */
        Y("y", 1),
        /**
         * z axis. value is 2 for indexing arrays.
         */
        Z("z", 2);

        /** string value */
        public final String axis;

        /** int value */
        public final int value;

        /**
         * constructor.
         * 
         * @param axis
         *            label for the axis
         * @param value
         *            serial number (starts at 0)
         */
        private Axis3(String axis, int value) {
            this.axis = axis;
            this.value = value;
        }
    }
}
