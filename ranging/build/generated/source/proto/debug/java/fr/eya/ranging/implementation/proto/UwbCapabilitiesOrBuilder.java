// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: uwbinfo.proto

package fr.eya.ranging.implementation.proto;

public interface UwbCapabilitiesOrBuilder extends
    // @@protoc_insertion_point(interface_extends:uwblink_app.UwbCapabilities)
    com.google.protobuf.MessageLiteOrBuilder {

  /**
   * <code>repeated int32 supported_config_ids = 1;</code>
   * @return A list containing the supportedConfigIds.
   */
  java.util.List<java.lang.Integer> getSupportedConfigIdsList();
  /**
   * <code>repeated int32 supported_config_ids = 1;</code>
   * @return The count of supportedConfigIds.
   */
  int getSupportedConfigIdsCount();
  /**
   * <code>repeated int32 supported_config_ids = 1;</code>
   * @param index The index of the element to return.
   * @return The supportedConfigIds at the given index.
   */
  int getSupportedConfigIds(int index);

  /**
   * <code>bool supports_azimuth = 2;</code>
   * @return The supportsAzimuth.
   */
  boolean getSupportsAzimuth();

  /**
   * <code>bool supports_elevation = 3;</code>
   * @return The supportsElevation.
   */
  boolean getSupportsElevation();
}
