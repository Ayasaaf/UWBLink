package fr.eya.uwblink.ui.Bluetooth.componets;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import fr.eya.uwblink.uwbranging.BluetoothChat.domain.chat.BluetoothController;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class BluetoothViewModel_Factory implements Factory<BluetoothViewModel> {
  private final Provider<BluetoothController> bluetoothControllerProvider;

  public BluetoothViewModel_Factory(Provider<BluetoothController> bluetoothControllerProvider) {
    this.bluetoothControllerProvider = bluetoothControllerProvider;
  }

  @Override
  public BluetoothViewModel get() {
    return newInstance(bluetoothControllerProvider.get());
  }

  public static BluetoothViewModel_Factory create(
      Provider<BluetoothController> bluetoothControllerProvider) {
    return new BluetoothViewModel_Factory(bluetoothControllerProvider);
  }

  public static BluetoothViewModel newInstance(BluetoothController bluetoothController) {
    return new BluetoothViewModel(bluetoothController);
  }
}
