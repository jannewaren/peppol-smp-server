/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package com.helger.peppol.smpserver.domain.redirect;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.compare.IComparator;
import com.helger.commons.id.IHasID;
import com.helger.peppol.identifier.generic.doctype.IDocumentTypeIdentifier;
import com.helger.peppol.smpserver.domain.extension.ISMPHasExtension;
import com.helger.peppol.smpserver.domain.servicegroup.ISMPServiceGroup;

/**
 * This interface represents a single SMP redirect for a certain document type
 * identifier ({@link IDocumentTypeIdentifier}).
 *
 * @author Philip Helger
 */
public interface ISMPRedirect extends IHasID <String>, Serializable, ISMPHasExtension
{
  /**
   * @return The service group which this redirect should handle.
   */
  @Nonnull
  ISMPServiceGroup getServiceGroup ();

  /**
   * @return The ID of the service group to which this redirect belongs. Never
   *         <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getServiceGroupID ();

  /**
   * @return The document type identifier of this redirect. Never
   *         <code>null</code>.
   */
  @Nonnull
  IDocumentTypeIdentifier getDocumentTypeIdentifier ();

  /**
   * @return The destination href of the new SMP. Never <code>null</code>.
   */
  @Nonnull
  @Nonempty
  String getTargetHref ();

  /**
   * @return The subject unique identifier of the target SMPs certificate used
   *         to sign its resources.
   */
  @Nonnull
  @Nonempty
  String getSubjectUniqueIdentifier ();

  /**
   * @return This redirect object as a PEPPOL SMP JAXB object for the REST
   *         interface. Never <code>null</code>.
   */
  @Nonnull
  com.helger.peppol.smp.ServiceMetadataType getAsJAXBObjectPeppol ();

  /**
   * @return This redirect object as a BDXR SMP JAXB object for the REST
   *         interface. Never <code>null</code>.
   */
  @Nonnull
  com.helger.peppol.bdxr.ServiceMetadataType getAsJAXBObjectBDXR ();

  @Nonnull
  static IComparator <ISMPRedirect> comparator ()
  {
    return (aElement1, aElement2) -> {
      int ret = aElement1.getServiceGroupID ().compareTo (aElement2.getServiceGroupID ());
      if (ret == 0)
        ret = aElement1.getDocumentTypeIdentifier ().compareTo (aElement2.getDocumentTypeIdentifier ());
      return ret;
    };
  }
}
