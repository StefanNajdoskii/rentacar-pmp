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
public final class BookingDao_Impl implements BookingDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BookingEntity> __insertionAdapterOfBookingEntity;

  private final EntityDeletionOrUpdateAdapter<BookingEntity> __deletionAdapterOfBookingEntity;

  private final EntityDeletionOrUpdateAdapter<BookingEntity> __updateAdapterOfBookingEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookingsForUser;

  public BookingDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookingEntity = new EntityInsertionAdapter<BookingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `bookings` (`id`,`userId`,`carId`,`carBrand`,`carModel`,`carImageUrl`,`startDate`,`endDate`,`totalPrice`,`status`,`pickupLocation`,`paymentStatus`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getCarId());
        statement.bindString(4, entity.getCarBrand());
        statement.bindString(5, entity.getCarModel());
        statement.bindString(6, entity.getCarImageUrl());
        statement.bindLong(7, entity.getStartDate());
        statement.bindLong(8, entity.getEndDate());
        statement.bindDouble(9, entity.getTotalPrice());
        statement.bindString(10, entity.getStatus());
        statement.bindString(11, entity.getPickupLocation());
        statement.bindString(12, entity.getPaymentStatus());
        statement.bindLong(13, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfBookingEntity = new EntityDeletionOrUpdateAdapter<BookingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bookings` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfBookingEntity = new EntityDeletionOrUpdateAdapter<BookingEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `bookings` SET `id` = ?,`userId` = ?,`carId` = ?,`carBrand` = ?,`carModel` = ?,`carImageUrl` = ?,`startDate` = ?,`endDate` = ?,`totalPrice` = ?,`status` = ?,`pickupLocation` = ?,`paymentStatus` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUserId());
        statement.bindString(3, entity.getCarId());
        statement.bindString(4, entity.getCarBrand());
        statement.bindString(5, entity.getCarModel());
        statement.bindString(6, entity.getCarImageUrl());
        statement.bindLong(7, entity.getStartDate());
        statement.bindLong(8, entity.getEndDate());
        statement.bindDouble(9, entity.getTotalPrice());
        statement.bindString(10, entity.getStatus());
        statement.bindString(11, entity.getPickupLocation());
        statement.bindString(12, entity.getPaymentStatus());
        statement.bindLong(13, entity.getCreatedAt());
        statement.bindString(14, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBookingsForUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM bookings WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBooking(final BookingEntity booking,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookingEntity.insert(booking);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBookings(final List<BookingEntity> bookings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBookingEntity.insert(bookings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBooking(final BookingEntity booking,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookingEntity.handle(booking);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBooking(final BookingEntity booking,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBookingEntity.handle(booking);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookingsForUser(final String userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookingsForUser.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfDeleteBookingsForUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BookingEntity>> getBookingsForUser(final String userId) {
    final String _sql = "SELECT * FROM bookings WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"bookings"}, new Callable<List<BookingEntity>>() {
      @Override
      @NonNull
      public List<BookingEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCarId = CursorUtil.getColumnIndexOrThrow(_cursor, "carId");
          final int _cursorIndexOfCarBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "carBrand");
          final int _cursorIndexOfCarModel = CursorUtil.getColumnIndexOrThrow(_cursor, "carModel");
          final int _cursorIndexOfCarImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "carImageUrl");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfTotalPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPrice");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPickupLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "pickupLocation");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<BookingEntity> _result = new ArrayList<BookingEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookingEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpCarId;
            _tmpCarId = _cursor.getString(_cursorIndexOfCarId);
            final String _tmpCarBrand;
            _tmpCarBrand = _cursor.getString(_cursorIndexOfCarBrand);
            final String _tmpCarModel;
            _tmpCarModel = _cursor.getString(_cursorIndexOfCarModel);
            final String _tmpCarImageUrl;
            _tmpCarImageUrl = _cursor.getString(_cursorIndexOfCarImageUrl);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpTotalPrice;
            _tmpTotalPrice = _cursor.getDouble(_cursorIndexOfTotalPrice);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpPickupLocation;
            _tmpPickupLocation = _cursor.getString(_cursorIndexOfPickupLocation);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new BookingEntity(_tmpId,_tmpUserId,_tmpCarId,_tmpCarBrand,_tmpCarModel,_tmpCarImageUrl,_tmpStartDate,_tmpEndDate,_tmpTotalPrice,_tmpStatus,_tmpPickupLocation,_tmpPaymentStatus,_tmpCreatedAt);
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
  public Object getBookingById(final String bookingId,
      final Continuation<? super BookingEntity> $completion) {
    final String _sql = "SELECT * FROM bookings WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, bookingId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BookingEntity>() {
      @Override
      @Nullable
      public BookingEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfCarId = CursorUtil.getColumnIndexOrThrow(_cursor, "carId");
          final int _cursorIndexOfCarBrand = CursorUtil.getColumnIndexOrThrow(_cursor, "carBrand");
          final int _cursorIndexOfCarModel = CursorUtil.getColumnIndexOrThrow(_cursor, "carModel");
          final int _cursorIndexOfCarImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "carImageUrl");
          final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "startDate");
          final int _cursorIndexOfEndDate = CursorUtil.getColumnIndexOrThrow(_cursor, "endDate");
          final int _cursorIndexOfTotalPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPrice");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfPickupLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "pickupLocation");
          final int _cursorIndexOfPaymentStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentStatus");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final BookingEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final String _tmpCarId;
            _tmpCarId = _cursor.getString(_cursorIndexOfCarId);
            final String _tmpCarBrand;
            _tmpCarBrand = _cursor.getString(_cursorIndexOfCarBrand);
            final String _tmpCarModel;
            _tmpCarModel = _cursor.getString(_cursorIndexOfCarModel);
            final String _tmpCarImageUrl;
            _tmpCarImageUrl = _cursor.getString(_cursorIndexOfCarImageUrl);
            final long _tmpStartDate;
            _tmpStartDate = _cursor.getLong(_cursorIndexOfStartDate);
            final long _tmpEndDate;
            _tmpEndDate = _cursor.getLong(_cursorIndexOfEndDate);
            final double _tmpTotalPrice;
            _tmpTotalPrice = _cursor.getDouble(_cursorIndexOfTotalPrice);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpPickupLocation;
            _tmpPickupLocation = _cursor.getString(_cursorIndexOfPickupLocation);
            final String _tmpPaymentStatus;
            _tmpPaymentStatus = _cursor.getString(_cursorIndexOfPaymentStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new BookingEntity(_tmpId,_tmpUserId,_tmpCarId,_tmpCarBrand,_tmpCarModel,_tmpCarImageUrl,_tmpStartDate,_tmpEndDate,_tmpTotalPrice,_tmpStatus,_tmpPickupLocation,_tmpPaymentStatus,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
