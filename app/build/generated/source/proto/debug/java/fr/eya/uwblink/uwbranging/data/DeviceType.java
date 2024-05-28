// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: AppSettings.proto

package fr.eya.uwblink.uwbranging.data;

/**
 * Protobuf enum {@code DeviceType}
 */
public enum DeviceType
    implements com.google.protobuf.Internal.EnumLite {
  /**
   * <code>CONTROLLER = 0;</code>
   */
  CONTROLLER(0),
  /**
   * <code>CONTROLLEE = 1;</code>
   */
  CONTROLLEE(1),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>CONTROLLER = 0;</code>
   */
  public static final int CONTROLLER_VALUE = 0;
  /**
   * <code>CONTROLLEE = 1;</code>
   */
  public static final int CONTROLLEE_VALUE = 1;


  @java.lang.Override
  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The number of the enum to look for.
   * @return The enum associated with the given number.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static DeviceType valueOf(int value) {
    return forNumber(value);
  }

  public static DeviceType forNumber(int value) {
    switch (value) {
      case 0: return CONTROLLER;
      case 1: return CONTROLLEE;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<DeviceType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      DeviceType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<DeviceType>() {
          @java.lang.Override
          public DeviceType findValueByNumber(int number) {
            return DeviceType.forNumber(number);
          }
        };

  public static com.google.protobuf.Internal.EnumVerifier 
      internalGetVerifier() {
    return DeviceTypeVerifier.INSTANCE;
  }

  private static final class DeviceTypeVerifier implements 
       com.google.protobuf.Internal.EnumVerifier { 
          static final com.google.protobuf.Internal.EnumVerifier           INSTANCE = new DeviceTypeVerifier();
          @java.lang.Override
          public boolean isInRange(int number) {
            return DeviceType.forNumber(number) != null;
          }
        };

  private final int value;

  private DeviceType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:DeviceType)
}

