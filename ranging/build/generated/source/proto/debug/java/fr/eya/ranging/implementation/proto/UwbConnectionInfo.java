// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: uwbinfo.proto

package fr.eya.ranging.implementation.proto;

/**
 * <pre>
 * Connection info can be capabilities or configuration.
 * </pre>
 *
 * Protobuf type {@code uwblink_app.UwbConnectionInfo}
 */
public  final class UwbConnectionInfo extends
    com.google.protobuf.GeneratedMessageLite<
        UwbConnectionInfo, UwbConnectionInfo.Builder> implements
    // @@protoc_insertion_point(message_implements:uwblink_app.UwbConnectionInfo)
    UwbConnectionInfoOrBuilder {
  private UwbConnectionInfo() {
  }
  private int infoCase_ = 0;
  private java.lang.Object info_;
  public enum InfoCase {
    CAPABILITIES(1),
    CONFIGURATION(2),
    INFO_NOT_SET(0);
    private final int value;
    private InfoCase(int value) {
      this.value = value;
    }
    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static InfoCase valueOf(int value) {
      return forNumber(value);
    }

    public static InfoCase forNumber(int value) {
      switch (value) {
        case 1: return CAPABILITIES;
        case 2: return CONFIGURATION;
        case 0: return INFO_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  @java.lang.Override
  public InfoCase
  getInfoCase() {
    return InfoCase.forNumber(
        infoCase_);
  }

  private void clearInfo() {
    infoCase_ = 0;
    info_ = null;
  }

  public static final int CAPABILITIES_FIELD_NUMBER = 1;
  /**
   * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
   */
  @java.lang.Override
  public boolean hasCapabilities() {
    return infoCase_ == 1;
  }
  /**
   * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
   */
  @java.lang.Override
  public fr.eya.ranging.implementation.proto.UwbCapabilities getCapabilities() {
    if (infoCase_ == 1) {
       return (fr.eya.ranging.implementation.proto.UwbCapabilities) info_;
    }
    return fr.eya.ranging.implementation.proto.UwbCapabilities.getDefaultInstance();
  }
  /**
   * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
   */
  private void setCapabilities(fr.eya.ranging.implementation.proto.UwbCapabilities value) {
    value.getClass();
  info_ = value;
    infoCase_ = 1;
  }
  /**
   * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
   */
  private void mergeCapabilities(fr.eya.ranging.implementation.proto.UwbCapabilities value) {
    value.getClass();
  if (infoCase_ == 1 &&
        info_ != fr.eya.ranging.implementation.proto.UwbCapabilities.getDefaultInstance()) {
      info_ = fr.eya.ranging.implementation.proto.UwbCapabilities.newBuilder((fr.eya.ranging.implementation.proto.UwbCapabilities) info_)
          .mergeFrom(value).buildPartial();
    } else {
      info_ = value;
    }
    infoCase_ = 1;
  }
  /**
   * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
   */
  private void clearCapabilities() {
    if (infoCase_ == 1) {
      infoCase_ = 0;
      info_ = null;
    }
  }

  public static final int CONFIGURATION_FIELD_NUMBER = 2;
  /**
   * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
   */
  @java.lang.Override
  public boolean hasConfiguration() {
    return infoCase_ == 2;
  }
  /**
   * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
   */
  @java.lang.Override
  public fr.eya.ranging.implementation.proto.UwbConfiguration getConfiguration() {
    if (infoCase_ == 2) {
       return (fr.eya.ranging.implementation.proto.UwbConfiguration) info_;
    }
    return fr.eya.ranging.implementation.proto.UwbConfiguration.getDefaultInstance();
  }
  /**
   * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
   */
  private void setConfiguration(fr.eya.ranging.implementation.proto.UwbConfiguration value) {
    value.getClass();
  info_ = value;
    infoCase_ = 2;
  }
  /**
   * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
   */
  private void mergeConfiguration(fr.eya.ranging.implementation.proto.UwbConfiguration value) {
    value.getClass();
  if (infoCase_ == 2 &&
        info_ != fr.eya.ranging.implementation.proto.UwbConfiguration.getDefaultInstance()) {
      info_ = fr.eya.ranging.implementation.proto.UwbConfiguration.newBuilder((fr.eya.ranging.implementation.proto.UwbConfiguration) info_)
          .mergeFrom(value).buildPartial();
    } else {
      info_ = value;
    }
    infoCase_ = 2;
  }
  /**
   * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
   */
  private void clearConfiguration() {
    if (infoCase_ == 2) {
      infoCase_ = 0;
      info_ = null;
    }
  }

  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(fr.eya.ranging.implementation.proto.UwbConnectionInfo prototype) {
    return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * <pre>
   * Connection info can be capabilities or configuration.
   * </pre>
   *
   * Protobuf type {@code uwblink_app.UwbConnectionInfo}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        fr.eya.ranging.implementation.proto.UwbConnectionInfo, Builder> implements
      // @@protoc_insertion_point(builder_implements:uwblink_app.UwbConnectionInfo)
      fr.eya.ranging.implementation.proto.UwbConnectionInfoOrBuilder {
    // Construct using fr.eya.ranging.implementation.proto.UwbConnectionInfo.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }

    @java.lang.Override
    public InfoCase
        getInfoCase() {
      return instance.getInfoCase();
    }

    public Builder clearInfo() {
      copyOnWrite();
      instance.clearInfo();
      return this;
    }


    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    @java.lang.Override
    public boolean hasCapabilities() {
      return instance.hasCapabilities();
    }
    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    @java.lang.Override
    public fr.eya.ranging.implementation.proto.UwbCapabilities getCapabilities() {
      return instance.getCapabilities();
    }
    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    public Builder setCapabilities(fr.eya.ranging.implementation.proto.UwbCapabilities value) {
      copyOnWrite();
      instance.setCapabilities(value);
      return this;
    }
    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    public Builder setCapabilities(
        fr.eya.ranging.implementation.proto.UwbCapabilities.Builder builderForValue) {
      copyOnWrite();
      instance.setCapabilities(builderForValue.build());
      return this;
    }
    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    public Builder mergeCapabilities(fr.eya.ranging.implementation.proto.UwbCapabilities value) {
      copyOnWrite();
      instance.mergeCapabilities(value);
      return this;
    }
    /**
     * <code>.uwblink_app.UwbCapabilities capabilities = 1;</code>
     */
    public Builder clearCapabilities() {
      copyOnWrite();
      instance.clearCapabilities();
      return this;
    }

    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    @java.lang.Override
    public boolean hasConfiguration() {
      return instance.hasConfiguration();
    }
    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    @java.lang.Override
    public fr.eya.ranging.implementation.proto.UwbConfiguration getConfiguration() {
      return instance.getConfiguration();
    }
    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    public Builder setConfiguration(fr.eya.ranging.implementation.proto.UwbConfiguration value) {
      copyOnWrite();
      instance.setConfiguration(value);
      return this;
    }
    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    public Builder setConfiguration(
        fr.eya.ranging.implementation.proto.UwbConfiguration.Builder builderForValue) {
      copyOnWrite();
      instance.setConfiguration(builderForValue.build());
      return this;
    }
    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    public Builder mergeConfiguration(fr.eya.ranging.implementation.proto.UwbConfiguration value) {
      copyOnWrite();
      instance.mergeConfiguration(value);
      return this;
    }
    /**
     * <code>.uwblink_app.UwbConfiguration configuration = 2;</code>
     */
    public Builder clearConfiguration() {
      copyOnWrite();
      instance.clearConfiguration();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:uwblink_app.UwbConnectionInfo)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new fr.eya.ranging.implementation.proto.UwbConnectionInfo();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "info_",
            "infoCase_",
            fr.eya.ranging.implementation.proto.UwbCapabilities.class,
            fr.eya.ranging.implementation.proto.UwbConfiguration.class,
          };
          java.lang.String info =
              "\u0000\u0002\u0001\u0000\u0001\u0002\u0002\u0000\u0000\u0000\u0001<\u0000\u0002<" +
              "\u0000";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<fr.eya.ranging.implementation.proto.UwbConnectionInfo> parser = PARSER;
        if (parser == null) {
          synchronized (fr.eya.ranging.implementation.proto.UwbConnectionInfo.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<fr.eya.ranging.implementation.proto.UwbConnectionInfo>(
                      DEFAULT_INSTANCE);
              PARSER = parser;
            }
          }
        }
        return parser;
    }
    case GET_MEMOIZED_IS_INITIALIZED: {
      return (byte) 1;
    }
    case SET_MEMOIZED_IS_INITIALIZED: {
      return null;
    }
    }
    throw new UnsupportedOperationException();
  }


  // @@protoc_insertion_point(class_scope:uwblink_app.UwbConnectionInfo)
  private static final fr.eya.ranging.implementation.proto.UwbConnectionInfo DEFAULT_INSTANCE;
  static {
    UwbConnectionInfo defaultInstance = new UwbConnectionInfo();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      UwbConnectionInfo.class, defaultInstance);
  }

  public static fr.eya.ranging.implementation.proto.UwbConnectionInfo getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<UwbConnectionInfo> PARSER;

  public static com.google.protobuf.Parser<UwbConnectionInfo> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}

