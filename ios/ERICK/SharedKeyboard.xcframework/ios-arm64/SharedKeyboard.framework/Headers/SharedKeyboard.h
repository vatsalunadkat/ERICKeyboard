#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedKeyboardColorEntry, SharedKeyboardKotlinEnumCompanion, SharedKeyboardKotlinEnum<E>, SharedKeyboardColorPaletteType, SharedKeyboardKotlinArray<T>, SharedKeyboardColorPalettes, SharedKeyboardDirection, SharedKeyboardSingleSwipeBinding, SharedKeyboardCustomLayout, SharedKeyboardCustomLayoutManagerCompanion, SharedKeyboardLayoutType, SharedKeyboardCustomLayoutSerializer, SharedKeyboardInputAction, SharedKeyboardKeyboardMode, SharedKeyboardKeyboardFactory, SharedKeyboardKeyboardStateMachine, SharedKeyboardSingleSwipeBindingCompanion, SharedKeyboardSingleSwipeBindingAction, SharedKeyboardSingleSwipeBindingCharacter, SharedKeyboardKotlinThrowable, SharedKeyboardKotlinException, SharedKeyboardKotlinRuntimeException, SharedKeyboardKotlinIllegalStateException;

@protocol SharedKeyboardPlatform, SharedKeyboardKotlinx_coroutines_coreFlow, SharedKeyboardKotlinComparable, SharedKeyboardCustomLayoutStorage, SharedKeyboardKeyboardActionDelegate, SharedKeyboardKotlinx_coroutines_coreCoroutineScope, SharedKeyboardKotlinx_coroutines_coreFlowCollector, SharedKeyboardKotlinIterator, SharedKeyboardKotlinCoroutineContext, SharedKeyboardKotlinCoroutineContextElement, SharedKeyboardKotlinCoroutineContextKey;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wincompatible-property-type"
#pragma clang diagnostic ignored "-Wnullability"

#pragma push_macro("_Nullable_result")
#if !__has_feature(nullability_nullable_result)
#undef _Nullable_result
#define _Nullable_result _Nullable
#endif

__attribute__((swift_name("KotlinBase")))
@interface SharedKeyboardBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end

@interface SharedKeyboardBase (SharedKeyboardBaseCopying) <NSCopying>
@end

__attribute__((swift_name("KotlinMutableSet")))
@interface SharedKeyboardMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end

__attribute__((swift_name("KotlinMutableDictionary")))
@interface SharedKeyboardMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end

@interface NSError (NSErrorSharedKeyboardKotlinException)
@property (readonly) id _Nullable kotlinException;
@end

__attribute__((swift_name("KotlinNumber")))
@interface SharedKeyboardNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end

__attribute__((swift_name("KotlinByte")))
@interface SharedKeyboardByte : SharedKeyboardNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end

__attribute__((swift_name("KotlinUByte")))
@interface SharedKeyboardUByte : SharedKeyboardNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end

__attribute__((swift_name("KotlinShort")))
@interface SharedKeyboardShort : SharedKeyboardNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end

__attribute__((swift_name("KotlinUShort")))
@interface SharedKeyboardUShort : SharedKeyboardNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end

__attribute__((swift_name("KotlinInt")))
@interface SharedKeyboardInt : SharedKeyboardNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end

__attribute__((swift_name("KotlinUInt")))
@interface SharedKeyboardUInt : SharedKeyboardNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end

__attribute__((swift_name("KotlinLong")))
@interface SharedKeyboardLong : SharedKeyboardNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end

__attribute__((swift_name("KotlinULong")))
@interface SharedKeyboardULong : SharedKeyboardNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end

__attribute__((swift_name("KotlinFloat")))
@interface SharedKeyboardFloat : SharedKeyboardNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end

__attribute__((swift_name("KotlinDouble")))
@interface SharedKeyboardDouble : SharedKeyboardNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end

__attribute__((swift_name("KotlinBoolean")))
@interface SharedKeyboardBoolean : SharedKeyboardNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end

__attribute__((swift_name("Platform")))
@protocol SharedKeyboardPlatform
@required
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("IOSPlatform")))
@interface SharedKeyboardIOSPlatform : SharedKeyboardBase <SharedKeyboardPlatform>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((swift_name("SettingsRepository")))
@protocol SharedKeyboardSettingsRepository
@required
- (void)setColorblindModeEnabled:(BOOL)enabled __attribute__((swift_name("setColorblindMode(enabled:)")));
- (void)setDarkThemeEnabled:(BOOL)enabled __attribute__((swift_name("setDarkTheme(enabled:)")));
- (void)setLayoutTypeLayoutType:(NSString *)layoutType __attribute__((swift_name("setLayoutType(layoutType:)")));
- (void)setLeftHandedModeEnabled:(BOOL)enabled __attribute__((swift_name("setLeftHandedMode(enabled:)")));
@property (readonly) id<SharedKeyboardKotlinx_coroutines_coreFlow> colorblindMode __attribute__((swift_name("colorblindMode")));
@property (readonly) id<SharedKeyboardKotlinx_coroutines_coreFlow> darkTheme __attribute__((swift_name("darkTheme")));
@property (readonly) id<SharedKeyboardKotlinx_coroutines_coreFlow> layoutType __attribute__((swift_name("layoutType")));
@property (readonly) id<SharedKeyboardKotlinx_coroutines_coreFlow> leftHandedMode __attribute__((swift_name("leftHandedMode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorEntry")))
@interface SharedKeyboardColorEntry : SharedKeyboardBase
- (instancetype)initWithName:(NSString *)name hex:(NSString *)hex __attribute__((swift_name("init(name:hex:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardColorEntry *)doCopyName:(NSString *)name hex:(NSString *)hex __attribute__((swift_name("doCopy(name:hex:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *hex __attribute__((swift_name("hex")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end

__attribute__((swift_name("KotlinComparable")))
@protocol SharedKeyboardKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end

__attribute__((swift_name("KotlinEnum")))
@interface SharedKeyboardKotlinEnum<E> : SharedKeyboardBase <SharedKeyboardKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKeyboardKotlinEnumCompanion *companion __attribute__((swift_name("companion")));
- (int32_t)compareToOther:(E)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorPaletteType")))
@interface SharedKeyboardColorPaletteType : SharedKeyboardKotlinEnum<SharedKeyboardColorPaletteType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardColorPaletteType *default_ __attribute__((swift_name("default_")));
@property (class, readonly) SharedKeyboardColorPaletteType *okabeIto __attribute__((swift_name("okabeIto")));
@property (class, readonly) SharedKeyboardColorPaletteType *deuteranopia __attribute__((swift_name("deuteranopia")));
@property (class, readonly) SharedKeyboardColorPaletteType *protanopia __attribute__((swift_name("protanopia")));
@property (class, readonly) SharedKeyboardColorPaletteType *tritanopia __attribute__((swift_name("tritanopia")));
@property (class, readonly) SharedKeyboardColorPaletteType *pastel __attribute__((swift_name("pastel")));
+ (SharedKeyboardKotlinArray<SharedKeyboardColorPaletteType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardColorPaletteType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorPalettes")))
@interface SharedKeyboardColorPalettes : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)colorPalettes __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardColorPalettes *shared __attribute__((swift_name("shared")));
- (NSString *)contrastTextColorHex:(NSString *)hex __attribute__((swift_name("contrastTextColor(hex:)")));
- (NSString *)getColorForDirectionHexDir:(SharedKeyboardDirection *)dir paletteType:(SharedKeyboardColorPaletteType *)paletteType __attribute__((swift_name("getColorForDirectionHex(dir:paletteType:)")));
- (NSArray<SharedKeyboardColorEntry *> *)getPaletteType:(SharedKeyboardColorPaletteType *)type __attribute__((swift_name("getPalette(type:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayout")))
@interface SharedKeyboardCustomLayout : SharedKeyboardBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name normalChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)normalChordMap shiftedChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)shiftedChordMap singleSwipeNormalMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeNormalMap singleSwipeShiftedMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeShiftedMap __attribute__((swift_name("init(id:name:normalChordMap:shiftedChordMap:singleSwipeNormalMap:singleSwipeShiftedMap:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardCustomLayout *)doCopyId:(NSString *)id name:(NSString *)name normalChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)normalChordMap shiftedChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)shiftedChordMap singleSwipeNormalMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeNormalMap singleSwipeShiftedMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeShiftedMap __attribute__((swift_name("doCopy(id:name:normalChordMap:shiftedChordMap:singleSwipeNormalMap:singleSwipeShiftedMap:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *normalChordMap __attribute__((swift_name("normalChordMap")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *shiftedChordMap __attribute__((swift_name("shiftedChordMap")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *singleSwipeNormalMap __attribute__((swift_name("singleSwipeNormalMap")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *singleSwipeShiftedMap __attribute__((swift_name("singleSwipeShiftedMap")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayoutManager")))
@interface SharedKeyboardCustomLayoutManager : SharedKeyboardBase
- (instancetype)initWithStorage:(id<SharedKeyboardCustomLayoutStorage>)storage __attribute__((swift_name("init(storage:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKeyboardCustomLayoutManagerCompanion *companion __attribute__((swift_name("companion")));
- (SharedKeyboardCustomLayout *)createBlankName:(NSString *)name __attribute__((swift_name("createBlank(name:)")));
- (void)deleteId:(NSString *)id __attribute__((swift_name("delete(id:)")));
- (SharedKeyboardCustomLayout *)duplicateFromBuiltInSourceLayout:(SharedKeyboardLayoutType *)sourceLayout customName:(NSString *)customName __attribute__((swift_name("duplicateFromBuiltIn(sourceLayout:customName:)")));
- (NSArray<SharedKeyboardCustomLayout *> *)getAll __attribute__((swift_name("getAll()")));
- (SharedKeyboardCustomLayout * _Nullable)getByIdId:(NSString *)id __attribute__((swift_name("getById(id:)")));
- (void)loadAll __attribute__((swift_name("loadAll()")));
- (BOOL)renameId:(NSString *)id newName:(NSString *)newName __attribute__((swift_name("rename(id:newName:)")));
- (NSArray<NSString *> *)saveLayout:(SharedKeyboardCustomLayout *)layout __attribute__((swift_name("save(layout:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayoutManager.Companion")))
@interface SharedKeyboardCustomLayoutManagerCompanion : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardCustomLayoutManagerCompanion *shared __attribute__((swift_name("shared")));
- (NSString *)generateId __attribute__((swift_name("generateId()")));
- (NSArray<NSString *> *)validateLayoutLayout:(SharedKeyboardCustomLayout *)layout __attribute__((swift_name("validateLayout(layout:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayoutSerializer")))
@interface SharedKeyboardCustomLayoutSerializer : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)customLayoutSerializer __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardCustomLayoutSerializer *shared __attribute__((swift_name("shared")));
- (NSArray<SharedKeyboardCustomLayout *> *)deserializeAllJson:(NSString *)json __attribute__((swift_name("deserializeAll(json:)")));
- (NSString *)serializeAllLayouts:(NSArray<SharedKeyboardCustomLayout *> *)layouts __attribute__((swift_name("serializeAll(layouts:)")));
@end

__attribute__((swift_name("CustomLayoutStorage")))
@protocol SharedKeyboardCustomLayoutStorage
@required
- (NSString *)loadAllLayoutsJson __attribute__((swift_name("loadAllLayoutsJson()")));
- (void)saveAllLayoutsJsonJson:(NSString *)json __attribute__((swift_name("saveAllLayoutsJson(json:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Direction")))
@interface SharedKeyboardDirection : SharedKeyboardKotlinEnum<SharedKeyboardDirection *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardDirection *none __attribute__((swift_name("none")));
@property (class, readonly) SharedKeyboardDirection *n __attribute__((swift_name("n")));
@property (class, readonly) SharedKeyboardDirection *ne __attribute__((swift_name("ne")));
@property (class, readonly) SharedKeyboardDirection *e __attribute__((swift_name("e")));
@property (class, readonly) SharedKeyboardDirection *se __attribute__((swift_name("se")));
@property (class, readonly) SharedKeyboardDirection *s __attribute__((swift_name("s")));
@property (class, readonly) SharedKeyboardDirection *sw __attribute__((swift_name("sw")));
@property (class, readonly) SharedKeyboardDirection *w __attribute__((swift_name("w")));
@property (class, readonly) SharedKeyboardDirection *nw __attribute__((swift_name("nw")));
+ (SharedKeyboardKotlinArray<SharedKeyboardDirection *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardDirection *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InputAction")))
@interface SharedKeyboardInputAction : SharedKeyboardKotlinEnum<SharedKeyboardInputAction *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardInputAction *space __attribute__((swift_name("space")));
@property (class, readonly) SharedKeyboardInputAction *enter __attribute__((swift_name("enter")));
@property (class, readonly) SharedKeyboardInputAction *backspace __attribute__((swift_name("backspace")));
@property (class, readonly) SharedKeyboardInputAction *deleteForward __attribute__((swift_name("deleteForward")));
@property (class, readonly) SharedKeyboardInputAction *toggleShift __attribute__((swift_name("toggleShift")));
@property (class, readonly) SharedKeyboardInputAction *toggleCaps __attribute__((swift_name("toggleCaps")));
@property (class, readonly) SharedKeyboardInputAction *moveHome __attribute__((swift_name("moveHome")));
@property (class, readonly) SharedKeyboardInputAction *moveEnd __attribute__((swift_name("moveEnd")));
@property (class, readonly) SharedKeyboardInputAction *dpadUp __attribute__((swift_name("dpadUp")));
@property (class, readonly) SharedKeyboardInputAction *dpadDown __attribute__((swift_name("dpadDown")));
@property (class, readonly) SharedKeyboardInputAction *dpadLeft __attribute__((swift_name("dpadLeft")));
@property (class, readonly) SharedKeyboardInputAction *dpadRight __attribute__((swift_name("dpadRight")));
@property (class, readonly) SharedKeyboardInputAction *pageUp __attribute__((swift_name("pageUp")));
@property (class, readonly) SharedKeyboardInputAction *pageDown __attribute__((swift_name("pageDown")));
@property (class, readonly) SharedKeyboardInputAction *tab __attribute__((swift_name("tab")));
+ (SharedKeyboardKotlinArray<SharedKeyboardInputAction *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardInputAction *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("KeyboardActionDelegate")))
@protocol SharedKeyboardKeyboardActionDelegate
@required
- (void)commitTextText:(NSString *)text __attribute__((swift_name("commitText(text:)")));
- (void)onModeChangedMode:(SharedKeyboardKeyboardMode *)mode __attribute__((swift_name("onModeChanged(mode:)")));
- (void)sendInputActionAction:(SharedKeyboardInputAction *)action __attribute__((swift_name("sendInputAction(action:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardFactory")))
@interface SharedKeyboardKeyboardFactory : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)keyboardFactory __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardKeyboardFactory *shared __attribute__((swift_name("shared")));
- (SharedKeyboardKeyboardStateMachine *)createEngineDelegate:(id<SharedKeyboardKeyboardActionDelegate>)delegate __attribute__((swift_name("createEngine(delegate:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardLogic")))
@interface SharedKeyboardKeyboardLogic : SharedKeyboardBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (NSArray<NSString *> *)getCharactersForDirectionDir:(SharedKeyboardDirection *)dir mode:(SharedKeyboardKeyboardMode *)mode layout:(SharedKeyboardLayoutType *)layout __attribute__((swift_name("getCharactersForDirection(dir:mode:layout:)")));
- (NSString *)getChordResultLeftDir:(SharedKeyboardDirection *)leftDir rightDir:(SharedKeyboardDirection *)rightDir mode:(SharedKeyboardKeyboardMode *)mode layout:(SharedKeyboardLayoutType *)layout __attribute__((swift_name("getChordResult(leftDir:rightDir:mode:layout:)")));
- (SharedKeyboardDirection *)getDirectionFromXYX:(float)x y:(float)y __attribute__((swift_name("getDirectionFromXY(x:y:)")));
- (SharedKeyboardInputAction * _Nullable)getDoubleSwipeActionDir:(SharedKeyboardDirection *)dir __attribute__((swift_name("getDoubleSwipeAction(dir:)")));
- (id _Nullable)getSingleSwipeResultDir:(SharedKeyboardDirection *)dir mode:(SharedKeyboardKeyboardMode *)mode __attribute__((swift_name("getSingleSwipeResult(dir:mode:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardMode")))
@interface SharedKeyboardKeyboardMode : SharedKeyboardKotlinEnum<SharedKeyboardKeyboardMode *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardKeyboardMode *normal __attribute__((swift_name("normal")));
@property (class, readonly) SharedKeyboardKeyboardMode *shifted __attribute__((swift_name("shifted")));
@property (class, readonly) SharedKeyboardKeyboardMode *capsLocked __attribute__((swift_name("capsLocked")));
+ (SharedKeyboardKotlinArray<SharedKeyboardKeyboardMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardKeyboardMode *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardStateMachine")))
@interface SharedKeyboardKeyboardStateMachine : SharedKeyboardBase
- (instancetype)initWithDelegate:(id<SharedKeyboardKeyboardActionDelegate>)delegate coroutineScope:(id<SharedKeyboardKotlinx_coroutines_coreCoroutineScope>)coroutineScope __attribute__((swift_name("init(delegate:coroutineScope:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardKeyboardStateMachine *)createKeyboardStateMachineForIOSDelegate:(id<SharedKeyboardKeyboardActionDelegate>)delegate __attribute__((swift_name("createKeyboardStateMachineForIOS(delegate:)")));
- (NSArray<NSString *> *)getCharactersForDirectionDir:(SharedKeyboardDirection *)dir __attribute__((swift_name("getCharactersForDirection(dir:)")));
- (NSArray<SharedKeyboardColorEntry *> *)getCurrentPalette __attribute__((swift_name("getCurrentPalette()")));
- (NSString *)getPreviewText __attribute__((swift_name("getPreviewText()")));
- (void)handleTouchX:(float)x y:(float)y isLeft:(BOOL)isLeft actionDownOrMove:(BOOL)actionDownOrMove actionUp:(BOOL)actionUp __attribute__((swift_name("handleTouch(x:y:isLeft:actionDownOrMove:actionUp:)")));
- (void)setColorPalettePalette:(SharedKeyboardColorPaletteType *)palette __attribute__((swift_name("setColorPalette(palette:)")));
- (void)setLayoutTypeLayout:(SharedKeyboardLayoutType *)layout __attribute__((swift_name("setLayoutType(layout:)")));
@property (readonly) SharedKeyboardLayoutType *currentLayoutType __attribute__((swift_name("currentLayoutType")));
@property (readonly) SharedKeyboardKeyboardMode *currentMode __attribute__((swift_name("currentMode")));
@property (readonly) SharedKeyboardColorPaletteType *currentPaletteType __attribute__((swift_name("currentPaletteType")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LayoutType")))
@interface SharedKeyboardLayoutType : SharedKeyboardKotlinEnum<SharedKeyboardLayoutType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardLayoutType *logical __attribute__((swift_name("logical")));
@property (class, readonly) SharedKeyboardLayoutType *efficiency __attribute__((swift_name("efficiency")));
+ (SharedKeyboardKotlinArray<SharedKeyboardLayoutType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardLayoutType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("SingleSwipeBinding")))
@interface SharedKeyboardSingleSwipeBinding : SharedKeyboardBase
@property (class, readonly, getter=companion) SharedKeyboardSingleSwipeBindingCompanion *companion __attribute__((swift_name("companion")));
- (NSString *)toSerializable __attribute__((swift_name("toSerializable()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SingleSwipeBinding.Action")))
@interface SharedKeyboardSingleSwipeBindingAction : SharedKeyboardSingleSwipeBinding
- (instancetype)initWithAction:(SharedKeyboardInputAction *)action __attribute__((swift_name("init(action:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardSingleSwipeBindingAction *)doCopyAction:(SharedKeyboardInputAction *)action __attribute__((swift_name("doCopy(action:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKeyboardInputAction *action __attribute__((swift_name("action")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SingleSwipeBinding.Character")))
@interface SharedKeyboardSingleSwipeBindingCharacter : SharedKeyboardSingleSwipeBinding
- (instancetype)initWithChar:(NSString *)char_ __attribute__((swift_name("init(char:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardSingleSwipeBindingCharacter *)doCopyChar:(NSString *)char_ __attribute__((swift_name("doCopy(char:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly, getter=char) NSString *char_ __attribute__((swift_name("char_")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SingleSwipeBinding.Companion")))
@interface SharedKeyboardSingleSwipeBindingCompanion : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardSingleSwipeBindingCompanion *shared __attribute__((swift_name("shared")));
- (SharedKeyboardSingleSwipeBinding * _Nullable)fromSerializableS:(NSString *)s __attribute__((swift_name("fromSerializable(s:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Platform_iosKt")))
@interface SharedKeyboardPlatform_iosKt : SharedKeyboardBase
+ (id<SharedKeyboardPlatform>)getPlatform __attribute__((swift_name("getPlatform()")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlow")))
@protocol SharedKeyboardKotlinx_coroutines_coreFlow
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)collectCollector:(id<SharedKeyboardKotlinx_coroutines_coreFlowCollector>)collector completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("collect(collector:completionHandler:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinEnumCompanion")))
@interface SharedKeyboardKotlinEnumCompanion : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardKotlinEnumCompanion *shared __attribute__((swift_name("shared")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface SharedKeyboardKotlinArray<T> : SharedKeyboardBase
+ (instancetype)arrayWithSize:(int32_t)size init:(T _Nullable (^)(SharedKeyboardInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (T _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<SharedKeyboardKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(T _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreCoroutineScope")))
@protocol SharedKeyboardKotlinx_coroutines_coreCoroutineScope
@required
@property (readonly) id<SharedKeyboardKotlinCoroutineContext> coroutineContext __attribute__((swift_name("coroutineContext")));
@end

__attribute__((swift_name("KotlinThrowable")))
@interface SharedKeyboardKotlinThrowable : SharedKeyboardBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));

/**
 * @note annotations
 *   kotlin.experimental.ExperimentalNativeApi
*/
- (SharedKeyboardKotlinArray<NSString *> *)getStackTrace __attribute__((swift_name("getStackTrace()")));
- (void)printStackTrace __attribute__((swift_name("printStackTrace()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKeyboardKotlinThrowable * _Nullable cause __attribute__((swift_name("cause")));
@property (readonly) NSString * _Nullable message __attribute__((swift_name("message")));
- (NSError *)asError __attribute__((swift_name("asError()")));
@end

__attribute__((swift_name("KotlinException")))
@interface SharedKeyboardKotlinException : SharedKeyboardKotlinThrowable
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinRuntimeException")))
@interface SharedKeyboardKotlinRuntimeException : SharedKeyboardKotlinException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("KotlinIllegalStateException")))
@interface SharedKeyboardKotlinIllegalStateException : SharedKeyboardKotlinRuntimeException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.4")
*/
__attribute__((swift_name("KotlinCancellationException")))
@interface SharedKeyboardKotlinCancellationException : SharedKeyboardKotlinIllegalStateException
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (instancetype)initWithMessage:(NSString * _Nullable)message __attribute__((swift_name("init(message:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithCause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(cause:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithMessage:(NSString * _Nullable)message cause:(SharedKeyboardKotlinThrowable * _Nullable)cause __attribute__((swift_name("init(message:cause:)"))) __attribute__((objc_designated_initializer));
@end

__attribute__((swift_name("Kotlinx_coroutines_coreFlowCollector")))
@protocol SharedKeyboardKotlinx_coroutines_coreFlowCollector
@required

/**
 * @note This method converts instances of CancellationException to errors.
 * Other uncaught Kotlin exceptions are fatal.
*/
- (void)emitValue:(id _Nullable)value completionHandler:(void (^)(NSError * _Nullable))completionHandler __attribute__((swift_name("emit(value:completionHandler:)")));
@end

__attribute__((swift_name("KotlinIterator")))
@protocol SharedKeyboardKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end


/**
 * @note annotations
 *   kotlin.SinceKotlin(version="1.3")
*/
__attribute__((swift_name("KotlinCoroutineContext")))
@protocol SharedKeyboardKotlinCoroutineContext
@required
- (id _Nullable)foldInitial:(id _Nullable)initial operation:(id _Nullable (^)(id _Nullable, id<SharedKeyboardKotlinCoroutineContextElement>))operation __attribute__((swift_name("fold(initial:operation:)")));
- (id<SharedKeyboardKotlinCoroutineContextElement> _Nullable)getKey:(id<SharedKeyboardKotlinCoroutineContextKey>)key __attribute__((swift_name("get(key:)")));
- (id<SharedKeyboardKotlinCoroutineContext>)minusKeyKey:(id<SharedKeyboardKotlinCoroutineContextKey>)key __attribute__((swift_name("minusKey(key:)")));
- (id<SharedKeyboardKotlinCoroutineContext>)plusContext:(id<SharedKeyboardKotlinCoroutineContext>)context __attribute__((swift_name("plus(context:)")));
@end

__attribute__((swift_name("KotlinCoroutineContextElement")))
@protocol SharedKeyboardKotlinCoroutineContextElement <SharedKeyboardKotlinCoroutineContext>
@required
@property (readonly) id<SharedKeyboardKotlinCoroutineContextKey> key __attribute__((swift_name("key")));
@end

__attribute__((swift_name("KotlinCoroutineContextKey")))
@protocol SharedKeyboardKotlinCoroutineContextKey
@required
@end

#pragma pop_macro("_Nullable_result")
#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
