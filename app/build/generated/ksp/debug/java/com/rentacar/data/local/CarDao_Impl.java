package com.rentacar.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CarDao_Impl implements CarDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CarEntity> __insertionAdapterOfCarEntity;

  private final EntityDeletionOrUpdateAdapter<CarEntity> __deletionAdapterOfCarEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllCars;

  public CarDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCarEntity = new EntityInsertionAdapter<CarEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cars` (`id`,`brand`,`model`,`year`,`pricePerDay`,`imageUrl`,`description`,`transmission`,`fuelType`,`seats`,`available`,`location`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CarEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getBrand());
        statement.bindString(3, entity.getModel());
        statement.bindLong(4, entity.getYear());
        statement.bindDouble(5, entity.getPricePerDay());
        statement.bindString(6, entity.getImageUrl());
        statement.bindString(7, entity.getDescription());
        statement.bindString(8, entity.getTransmission());
        statement.bindString(9, entity.getFuelType());
        statement.bindLong(10, entity.getSeats());
        final int _tmp = entity.getAvailable() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindString(12, entity.getLocation());
      }
    };
    this.__deletionAdapterOfCarEntity = new EntityDeletionOrUpdateAdapter<CarEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `cars` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CarEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteAllCars = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cars";
        return _query;
      }
    };
  }

  @Override
  public Object insertCars(final List<CarEntity> cars,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCarEntity.insert(cars);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertCar(final CarEntity car, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCarEntity.insert(car);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCar(final CarEntity car, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCarEntity.handle(car);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllCars(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllCars.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllCars.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CarEntity>> getAllCars() {
    final String _sql = "SELECT * FROM cars ORDER BY brand ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cars"}, new Callable<List<CarEntity>>() {
      @Override
      @NonNull
      public List<CarEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfPricePerDay = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerDay");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTransmission = CursorUtil.getColumnIndexOrThrow(_cursor, "transmission");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfSeats = CursorUtil.getColumnIndexOrThrow(_cursor, "seats");
          final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final List<CarEntity> _result = new ArrayList<CarEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CarEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final double _tmpPricePerDay;
            _tmpPricePerDay = _cursor.getDouble(_cursorIndexOfPricePerDay);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpTransmission;
            _tmpTransmission = _cursor.getString(_cursorIndexOfTransmission);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final int _tmpSeats;
            _tmpSeats = _cursor.getInt(_cursorIndexOfSeats);
            final boolean _tmpAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAvailable);
            _tmpAvailable = _tmp != 0;
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            _item = new CarEntity(_tmpId,_tmpBrand,_tmpModel,_tmpYear,_tmpPricePerDay,_tmpImageUrl,_tmpDescription,_tmpTransmission,_tmpFuelType,_tmpSeats,_tmpAvailable,_tmpLocation);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CarEntity>> getAvailableCars() {
    final String _sql = "SELECT * FROM cars WHERE available = 1 ORDER BY brand ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cars"}, new Callable<List<CarEntity>>() {
      @Override
      @NonNull
      public List<CarEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfPricePerDay = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerDay");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTransmission = CursorUtil.getColumnIndexOrThrow(_cursor, "transmission");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfSeats = CursorUtil.getColumnIndexOrThrow(_cursor, "seats");
          final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final List<CarEntity> _result = new ArrayList<CarEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CarEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final double _tmpPricePerDay;
            _tmpPricePerDay = _cursor.getDouble(_cursorIndexOfPricePerDay);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpTransmission;
            _tmpTransmission = _cursor.getString(_cursorIndexOfTransmission);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final int _tmpSeats;
            _tmpSeats = _cursor.getInt(_cursorIndexOfSeats);
            final boolean _tmpAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAvailable);
            _tmpAvailable = _tmp != 0;
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            _item = new CarEntity(_tmpId,_tmpBrand,_tmpModel,_tmpYear,_tmpPricePerDay,_tmpImageUrl,_tmpDescription,_tmpTransmission,_tmpFuelType,_tmpSeats,_tmpAvailable,_tmpLocation);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCarById(final String carId, final Continuation<? super CarEntity> $completion) {
    final String _sql = "SELECT * FROM cars WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, carId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CarEntity>() {
      @Override
      @Nullable
      public CarEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfPricePerDay = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerDay");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTransmission = CursorUtil.getColumnIndexOrThrow(_cursor, "transmission");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfSeats = CursorUtil.getColumnIndexOrThrow(_cursor, "seats");
          final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final CarEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final double _tmpPricePerDay;
            _tmpPricePerDay = _cursor.getDouble(_cursorIndexOfPricePerDay);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpTransmission;
            _tmpTransmission = _cursor.getString(_cursorIndexOfTransmission);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final int _tmpSeats;
            _tmpSeats = _cursor.getInt(_cursorIndexOfSeats);
            final boolean _tmpAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAvailable);
            _tmpAvailable = _tmp != 0;
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            _result = new CarEntity(_tmpId,_tmpBrand,_tmpModel,_tmpYear,_tmpPricePerDay,_tmpImageUrl,_tmpDescription,_tmpTransmission,_tmpFuelType,_tmpSeats,_tmpAvailable,_tmpLocation);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CarEntity>> searchCars(final String query) {
    final String _sql = "SELECT * FROM cars WHERE brand LIKE '%' || ? || '%' OR model LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cars"}, new Callable<List<CarEntity>>() {
      @Override
      @NonNull
      public List<CarEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "brand");
          final int _cursorIndexOfModel = CursorUtil.getColumnIndexOrThrow(_cursor, "model");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final int _cursorIndexOfPricePerDay = CursorUtil.getColumnIndexOrThrow(_cursor, "pricePerDay");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTransmission = CursorUtil.getColumnIndexOrThrow(_cursor, "transmission");
          final int _cursorIndexOfFuelType = CursorUtil.getColumnIndexOrThrow(_cursor, "fuelType");
          final int _cursorIndexOfSeats = CursorUtil.getColumnIndexOrThrow(_cursor, "seats");
          final int _cursorIndexOfAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "available");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final List<CarEntity> _result = new ArrayList<CarEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CarEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpBrand;
            _tmpBrand = _cursor.getString(_cursorIndexOfBrand);
            final String _tmpModel;
            _tmpModel = _cursor.getString(_cursorIndexOfModel);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            final double _tmpPricePerDay;
            _tmpPricePerDay = _cursor.getDouble(_cursorIndexOfPricePerDay);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpTransmission;
            _tmpTransmission = _cursor.getString(_cursorIndexOfTransmission);
            final String _tmpFuelType;
            _tmpFuelType = _cursor.getString(_cursorIndexOfFuelType);
            final int _tmpSeats;
            _tmpSeats = _cursor.getInt(_cursorIndexOfSeats);
            final boolean _tmpAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAvailable);
            _tmpAvailable = _tmp != 0;
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            _item = new CarEntity(_tmpId,_tmpBrand,_tmpModel,_tmpYear,_tmpPricePerDay,_tmpImageUrl,_tmpDescription,_tmpTransmission,_tmpFuelType,_tmpSeats,_tmpAvailable,_tmpLocation);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getCarCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM cars";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
