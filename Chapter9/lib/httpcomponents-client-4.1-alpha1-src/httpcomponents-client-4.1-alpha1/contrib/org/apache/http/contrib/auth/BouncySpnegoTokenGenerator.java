/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.http.contrib.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.auth.SpnegoTokenGenerator;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.util.ASN1Dump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Takes Kerberos ticket and wraps into a SPNEGO token. Leaving some optional fields out.
 * Uses bouncycastle libs need a bcprov.jar
 */
public class BouncySpnegoTokenGenerator implements SpnegoTokenGenerator {
    private static final Log LOG = LogFactory.getLog(BouncySpnegoTokenGenerator.class);
    
    private DERObjectIdentifier spnegoOid = new DERObjectIdentifier("1.3.6.1.5.5.2");
    private DERObjectIdentifier kerbOid = new DERObjectIdentifier("1.2.840.113554.1.2.2");

    public byte [] generateSpnegoDERObject(byte [] kerbTicket) throws IOException {
        LOG.debug("enter: generateSpnegoDERObject(byte [] kerbTicket)");
        DEROctetString ourKerberosTicket = new DEROctetString(kerbTicket);
        
        DERSequence kerbOidSeq = new DERSequence(kerbOid);
        DERTaggedObject tagged0 = new DERTaggedObject(0, kerbOidSeq);
//        DERBitString bits = new DERBitString(new byte[]{0x01});
//        DERTaggedObject tagged1 = new DERTaggedObject(1, bits);
        DERTaggedObject tagged2 = new DERTaggedObject(2, ourKerberosTicket);
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(tagged0);
//        v.add(tagged1);
        v.add(tagged2);
        DERSequence seq = new DERSequence(v);
        DERTaggedObject taggedSpnego = new DERTaggedObject(0, seq);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ASN1OutputStream asn1Out = new ASN1OutputStream(out);

        ASN1Object spnegoOIDASN1 =  (ASN1Object) spnegoOid.toASN1Object();
        ASN1Object taggedSpnegoASN1    =    (ASN1Object) taggedSpnego.toASN1Object();
        
        int length = spnegoOIDASN1.getDEREncoded().length + taggedSpnegoASN1.getDEREncoded().length;
        byte [] lenBytes = writeLength(length);
        byte[] appWrap = new byte[lenBytes.length + 1];
        
        appWrap[0] = 0x60;
        for(int i=1; i < appWrap.length; i++){
            appWrap[i] = lenBytes[i-1];
        }
        
        asn1Out.write(appWrap);
        asn1Out.writeObject(spnegoOid.toASN1Object());
        asn1Out.writeObject(taggedSpnego.toASN1Object());

        byte[] app = out.toByteArray();
        ASN1InputStream in = new ASN1InputStream(app);

        if( LOG.isDebugEnabled() ){
            dumpToken(app);
        }
        
        return in.readObject().getDEREncoded();
    }

    private byte [] writeLength(int length) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (length > 127) {
            int size = 1;
            int val = length;

            while ((val >>>= 8) != 0) {
                size++;
            }
            
            out.write((byte) (size | 0x80));

            for (int i = (size - 1) * 8; i >= 0; i -= 8) {
                out.write((byte) (length >> i));
            }
        } else {
            out.write((byte) length);
        }
        return out.toByteArray();
    }
    
    protected void dumpToken(byte [] token) throws IOException{
        int skip = 12;
        byte [] manipBytes = new byte[token.length - skip];
        for(int i=skip; i < token.length; i++){
            manipBytes[i-skip] = token[i];
        }
        ASN1InputStream ourSpnego = new ASN1InputStream( manipBytes );
        System.out.println(ASN1Dump.dumpAsString( ourSpnego.readObject() ) );
    }
    
}
