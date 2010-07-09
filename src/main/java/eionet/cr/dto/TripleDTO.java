/*
 * The contents of this file are subjectUri to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Jaanus Heinlaid, Tieto Eesti
 */
package eionet.cr.dto;

/**
 * 
 * @author <a href="mailto:jaanus.heinlaid@tietoenator.com">Jaanus Heinlaid</a>
 *
 */
public class TripleDTO {

	/** */
	private long subjectHash;
	private long predicateHash;
	private long objectHash;
	private long sourceHash;
	private long objectDerivSourceHash;
	private long objectSourceObjectHash;

	/** */
	private boolean isAnonymousSubject;
	private boolean isAnonymousObject;
	private boolean isLiteralObject;

	/** */
	private String object;
	private String objectLanguage;
	private Double objectDouble;
	
	/** */
	private long genTime;
	private long objectDerivGenTime;
	
	/** */
	private String subjectUri;
	private String predicateUri;
	private String sourceUri;
	private String objectDerivSourceUri;

	/**
	 * @return the subjectHash
	 */
	public long getSubjectHash() {
		return subjectHash;
	}
	/**
	 * @param subjectHash the subjectHash to set
	 */
	public void setSubjectHash(long subjectHash) {
		this.subjectHash = subjectHash;
	}
	/**
	 * @return the predicateHash
	 */
	public long getPredicateHash() {
		return predicateHash;
	}
	/**
	 * @param predicateHash the predicateHash to set
	 */
	public void setPredicateHash(long predicateHash) {
		this.predicateHash = predicateHash;
	}
	/**
	 * @return the objectHash
	 */
	public long getObjectHash() {
		return objectHash;
	}
	/**
	 * @param objectHash the objectHash to set
	 */
	public void setObjectHash(long objectHash) {
		this.objectHash = objectHash;
	}
	/**
	 * @return the sourceHash
	 */
	public long getSourceHash() {
		return sourceHash;
	}
	/**
	 * @param sourceHash the sourceHash to set
	 */
	public void setSourceHash(long sourceHash) {
		this.sourceHash = sourceHash;
	}
	/**
	 * @return the objectDerivSourceHash
	 */
	public long getObjectDerivSourceHash() {
		return objectDerivSourceHash;
	}
	/**
	 * @param objectDerivSourceHash the objectDerivSourceHash to set
	 */
	public void setObjectDerivSourceHash(long objectDerivSourceHash) {
		this.objectDerivSourceHash = objectDerivSourceHash;
	}
	/**
	 * @return the objectSourceObjectHash
	 */
	public long getObjectSourceObjectHash() {
		return objectSourceObjectHash;
	}
	/**
	 * @param objectSourceObjectHash the objectSourceObjectHash to set
	 */
	public void setObjectSourceObjectHash(long objectSourceObjectHash) {
		this.objectSourceObjectHash = objectSourceObjectHash;
	}
	/**
	 * @return the subjectUri
	 */
	public String getSubjectUri() {
		return subjectUri;
	}
	/**
	 * @param subjectUri the subjectUri to set
	 */
	public void setSubjectUri(String subjectUri) {
		this.subjectUri = subjectUri;
	}
	/**
	 * @return the predicateUri
	 */
	public String getPredicateUri() {
		return predicateUri;
	}
	/**
	 * @param predicateUri the predicateUri to set
	 */
	public void setPredicateUri(String predicateUri) {
		this.predicateUri = predicateUri;
	}
	/**
	 * @return the sourceUri
	 */
	public String getSourceUri() {
		return sourceUri;
	}
	/**
	 * @param sourceUri the sourceUri to set
	 */
	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}
	/**
	 * @return the objectDerivSourceUri
	 */
	public String getObjectDerivSourceUri() {
		return objectDerivSourceUri;
	}
	/**
	 * @param objectDerivSourceUri the objectDerivSourceUri to set
	 */
	public void setObjectDerivSourceUri(String objectDerivSourceUri) {
		this.objectDerivSourceUri = objectDerivSourceUri;
	}
	/**
	 * @return the isAnonymousSubject
	 */
	public boolean isAnonymousSubject() {
		return isAnonymousSubject;
	}
	/**
	 * @param isAnonymousSubject the isAnonymousSubject to set
	 */
	public void setAnonymousSubject(boolean isAnonymousSubject) {
		this.isAnonymousSubject = isAnonymousSubject;
	}
	/**
	 * @return the isAnonymousObject
	 */
	public boolean isAnonymousObject() {
		return isAnonymousObject;
	}
	/**
	 * @param isAnonymousObject the isAnonymousObject to set
	 */
	public void setAnonymousObject(boolean isAnonymousObject) {
		this.isAnonymousObject = isAnonymousObject;
	}
	/**
	 * @return the isLiteralObject
	 */
	public boolean isLiteralObject() {
		return isLiteralObject;
	}
	/**
	 * @param isLiteralObject the isLiteralObject to set
	 */
	public void setLiteralObject(boolean isLiteralObject) {
		this.isLiteralObject = isLiteralObject;
	}
	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}
	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}
	/**
	 * @return the objectLanguage
	 */
	public String getObjectLanguage() {
		return objectLanguage;
	}
	/**
	 * @param objectLanguage the objectLanguage to set
	 */
	public void setObjectLanguage(String objectLanguage) {
		this.objectLanguage = objectLanguage;
	}
	/**
	 * @return the objectDouble
	 */
	public Double getObjectDouble() {
		return objectDouble;
	}
	/**
	 * @param objectDouble the objectDouble to set
	 */
	public void setObjectDouble(Double objectDouble) {
		this.objectDouble = objectDouble;
	}
	/**
	 * @return the genTime
	 */
	public long getGenTime() {
		return genTime;
	}
	/**
	 * @param genTime the genTime to set
	 */
	public void setGenTime(long genTime) {
		this.genTime = genTime;
	}
	/**
	 * @return the objectDerivGenTime
	 */
	public long getObjectDerivGenTime() {
		return objectDerivGenTime;
	}
	/**
	 * @param objectDerivGenTime the objectDerivGenTime to set
	 */
	public void setObjectDerivGenTime(long objectDerivGenTime) {
		this.objectDerivGenTime = objectDerivGenTime;
	}
}