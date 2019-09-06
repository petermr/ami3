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

/** * @(#)RandomNumberGenerator.java * * Copyright (c) 2000 by Sundar Dorai-Raj
  * * @author Sundar Dorai-Raj
  * * Email: sdoraira@vt.edu
  * * This program is free software; you can redistribute it and/or
  * * modify it under the terms of the GNU General Public License 
  * * as published by the Free Software Foundation; either version 2 
  * * of the License, or (at your option) any later version, 
  * * provided that any use properly credits the author. 
  * * This program is distributed in the hope that it will be useful,
  * * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  * * GNU General Public License for more details at http://www.gnu.org * * */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/*
 * Reference.
 * M. Matsumoto and T. Nishimura,
 * "Mersenne Twister: A 623-Dimensionally Equidistributed Uniform
 * Pseudo-Random Number Generator",
 * ACM Transactions on Modeling and Computer Simulation,
 * Vol. 8, No. 1, January 1998, pp 3--30.
 */

public class RandomNumberGenerator implements java.io.Serializable {
  static final long serialVersionUID = 3905348978240129619L;

  // Period parameters
  private static final int N = 624;
  private static final int M = 397;
  private static final int MATRIX_A = 0x9908b0df;
  private static final int UPPER_MASK = 0x80000000;
  private static final int LOWER_MASK = 0x7fffffff;

  // Tempering parameters
  private static final int TEMPERING_MASK_B = 0x9d2c5680;
  private static final int TEMPERING_MASK_C = 0xefc60000;

  private int mt[]; // the array for the state vector
  private int mti; // mti==N+1 means mt[N] is not initialized
  private int mag01[];

  // a good initial seed (of int size, though stored in a long)
	@SuppressWarnings("unused")
  private static final long GOOD_SEED = 4357;

  private synchronized void writeObject(ObjectOutputStream out)
          throws IOException {
    // just so we're synchronized.
    out.defaultWriteObject();
  }

  private synchronized void readObject (ObjectInputStream in) 
          throws IOException, ClassNotFoundException {
    // just so we're synchronized.
    in.defaultReadObject();
  }

  protected synchronized void setSeed(long seed) {
    haveNextGaussian = false;

    mt = new int[N];
        
    // setting initial seeds to mt[N] using
    // the generator Line 25 of Table 1 in
    // [KNUTH 1981, The Art of Computer Programming
    //    Vol. 2 (2nd Ed.), pp102]

    // the 0xffffffff is commented out because in Java
    // ints are always 32 bits; hence i & 0xffffffff == i

    mt[0]= ((int)seed); // & 0xffffffff;

    for(mti = 1; mti < N; mti++)
        mt[mti] = (69069 * mt[mti-1]);

    // mag01[x] = x * MATRIX_A  for x=0,1
    mag01 = new int[2];
    mag01[0] = 0x0;
    mag01[1] = MATRIX_A;
  }
    
  protected synchronized int next(int bits) {
    int y;
    
    if(mti >= N) {
      int kk;
      for(kk = 0; kk < N - M; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk+1] & LOWER_MASK);
        mt[kk] = mt[kk+M] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      for(; kk < N-1; kk++) {
        y = (mt[kk] & UPPER_MASK) | (mt[kk+1] & LOWER_MASK);
        mt[kk] = mt[kk+(M-N)] ^ (y >>> 1) ^ mag01[y & 0x1];
      }
      y = (mt[N-1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
      mt[N-1] = mt[M-1] ^ (y >>> 1) ^ mag01[y & 0x1];
      mti = 0;
    }
    y = mt[mti++];
    y ^= y >>> 11;
    y ^= (y << 7) & TEMPERING_MASK_B;
    y ^= (y << 15) & TEMPERING_MASK_C;
    y ^= (y >>> 18);
    return y >>> (32 - bits);
  }

  public RandomNumberGenerator() { 
    this(System.currentTimeMillis());
  }

  public RandomNumberGenerator(long seed) {
    setSeed(seed);
  }

  // generate integers between 0 and 2^32
  public synchronized int nextInt() {
    return next(32);
  }

  // generate integers between 0 and n-1 (inclusive)
  public synchronized int nextInt(int n) {
    if (n <= 0) throw new IllegalArgumentException( "n must be positive" ); 
    if ((n & -n) == n) { 
      // i.e., n is a power of 2 
      return (int)((n * (long)next(31)) >> 31); 
    }
    int bits, val; 
    do {
      bits = next(31); 
      val = bits % n; 
    } 
    while(bits - val + (n-1) < 0); 
    return val; 
  } 

  // generate Poisson(lambda)
  // E(X)=lambda ; Var(X)=lambda
  public synchronized int nextPoisson(double lambda) {
    int v=-1;
    double l=Math.exp(-lambda),p;
    p=1.0;
    while (p>=l) {
      p*=nextUniform();
      v++;
    }
    return v;
  }

  // generate Poisson(1)
  // E(X)=1 ; Var(X)=1
  public synchronized int nextPoisson() {
    return nextPoisson(1);
  }

  // generate random boolean variables
  public synchronized boolean nextBoolean() {
    return (next(32) & 1 << 15) != 0;
  }

  // generates true with probability p
  public synchronized boolean nextBoolean(double p) {
    double u=nextUniform();
    if(u < p) return true;
    return false;
  }

  // generate U(0,1)
  // E(X)=1/2 ; Var(X)=1/12
  public synchronized double nextUniform() {
    long l = ((long)(next(26)) << 27) + next(27);
    return l / (double)(1L << 53);
  }

  // generate U(a,b)
  // E(X)=(b-a)/2 ; Var(X)=(b-a)^2/12
  public synchronized double nextUniform(double a,double b) {
    return a + (b-a)*nextUniform();
  }

  private double nextGaussian;
  private boolean haveNextGaussian = false;

  // generate N(0,1)
  // E(X)=1 ; Var(X)=1
  public synchronized double nextGaussian() {
    if (!haveNextGaussian) {
      double v1=nextUniform(),v2=nextUniform();
      double x1,x2;
      x1=Math.sqrt(-2*Math.log(v1))*Math.cos(2*Math.PI*v2);
      x2=Math.sqrt(-2*Math.log(v1))*Math.sin(2*Math.PI*v2);
      nextGaussian=x2;
      haveNextGaussian=true;
      return x1;
    }
    else {
      haveNextGaussian=false;
      return nextGaussian;
    }
  }

  // generate N(m,s2)
  // E(X)=m ; Var(X)=s2
  public synchronized double nextGaussian(double m,double s2) {
    return nextGaussian()*Math.sqrt(s2)+m;
  }

  // generate Gamma(1,1)
  // E(X)=1 ; Var(X)=1
  public synchronized double nextGamma() {
    return nextGamma(1,1,0);
  }

  // generate Gamma(alpha,beta)
  // E(X)=alpha*beta ; Var(X)=alpha*beta^2
  public synchronized double nextGamma(double alpha,double beta) {
    return nextGamma(alpha,beta,0);
  }

  // generate shifted-Gamma(alpha,beta)
  // E(X)=alpha*beta+lambda ; Var(X)=alpha*beta^2
  public synchronized double nextGamma(double alpha,double beta,double lambda) {
    double gamma=0;
    if(alpha <= 0 || beta <= 0)
      throw new IllegalArgumentException("alpha and beta must be strictly positive.");
    if (alpha < 1) {
      double b,p;
      boolean flag=false;
      b=1+alpha*Math.exp(-1);
      while(!flag) {
        p=b*nextUniform();
        if (p>1) {
          gamma=-Math.log((b-p)/alpha);
          if (nextUniform()<=Math.pow(gamma,alpha-1)) flag=true;
        }
        else {
          gamma=Math.pow(p,1/alpha);
          if (nextUniform()<=Math.exp(-gamma)) flag=true;
        }
      }
    }
    else if (alpha == 1)
      gamma=-Math.log(nextUniform());
    else {
      double y=-Math.log(nextUniform());
      while (nextUniform()>Math.pow(y*Math.exp(1-y),alpha-1))
        y=-Math.log(nextUniform());
      gamma=alpha*y;
    }
    return beta*gamma+lambda;
  }

  // generate Exp(1)
  // E(X)=1 ; Var(X)=1
  public synchronized double nextExp() {
    return nextGamma(1,1,0);
  }

  // generate Exp(beta)
  // E(X)=beta ; Var(X)=beta^2
  public synchronized double nextExp(double beta) {
    return nextGamma(1,beta,0);
  }

  // generate shifted-Exp(beta)
  // E(X)=beta+lambda ; Var(X)=beta^2
  public synchronized double nextExp(double beta,double lambda) {
    return nextGamma(1,beta,lambda);
  }

  // generate ChiSq(1)
  // E(X)=1 ; Var(X)=2
  public synchronized double nextChiSq() {
    return nextGamma(0.5,2,0);
  }

  // generate ChiSq(df)
  // E(X)=df ; Var(X)=2*df
  public synchronized double nextChiSq(int df) {
    return nextGamma(0.5*(double)df,2,0);
  }

  // generate shifted-ChiSq(df)
  // E(X)=df+lambda ; Var(X)=2*df
  public synchronized double nextChiSq(int df,double lambda) {
    return nextGamma(0.5*(double)df,2,lambda);
  }

  // generate Beta(alpha,beta)
  // E(X)=a/(a+b) ; Var(X)=ab/[(a+b+1)(a+b)^2]
  public synchronized double nextBeta(double alpha,double beta) {
    if(alpha <= 0 || beta <=0)
      throw new IllegalArgumentException("alpha and beta must be strictly positive.");
    if (alpha == 1 && beta == 1)
      return nextUniform();
    else if (alpha >= 1 && beta >=1) {
      double A=alpha-1,
             B=beta-1,
             C=A+B,
             L=C*Math.log(C),
             mu=A/C,
             sigma=0.5/Math.sqrt(C);
      double y=nextGaussian(),x=sigma*y+mu;
      while (x < 0 || x > 1) {
        y=nextGaussian();
        x=sigma*y+mu;
      }
      double u=nextUniform();
      while (Math.log(u) >= A*Math.log(x/A)+B*Math.log((1-x)/B)+L+0.5*y*y) {
        y=nextGaussian();
        x=sigma*y+mu;
        while (x < 0 || x > 1) {
          y=nextGaussian();
          x=sigma*y+mu;
        }
        u=nextUniform();
      }
      return x;
    }
    else {
      double v1=Math.pow(nextUniform(),1/alpha),
             v2=Math.pow(nextUniform(),1/beta);
      while (v1+v2>1) {
        v1=Math.pow(nextUniform(),1/alpha);
        v2=Math.pow(nextUniform(),1/beta);
      }
      return v1/(v1+v2);
    }
  }
}


