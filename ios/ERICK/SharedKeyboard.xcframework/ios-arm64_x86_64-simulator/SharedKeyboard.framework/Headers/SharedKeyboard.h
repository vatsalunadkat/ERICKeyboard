#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedKeyboardColorEntry, SharedKeyboardKotlinEnumCompanion, SharedKeyboardKotlinEnum<E>, SharedKeyboardColorPaletteType, SharedKeyboardKotlinArray<T>, SharedKeyboardColorPalettes, SharedKeyboardDirection, SharedKeyboardControllerButton, SharedKeyboardControllerConfusionAnalyzer, SharedKeyboardControllerConfusionDrillSample, SharedKeyboardControllerStickSnapshot, SharedKeyboardDialSectionMode, SharedKeyboardControllerPassiveSignal, SharedKeyboardControllerConfusionType, SharedKeyboardControllerInputProcessor, SharedKeyboardControllerState, SharedKeyboardSingleSwipeBinding, SharedKeyboardCustomLayout, SharedKeyboardCustomLayoutManagerCompanion, SharedKeyboardLayoutType, SharedKeyboardCustomLayoutSerializer, SharedKeyboardEmojiCatalogPayload, SharedKeyboardErickAppTranslations, SharedKeyboardKeyboardLanguage, SharedKeyboardInputAction, SharedKeyboardInputMode, SharedKeyboardKeyboardMode, SharedKeyboardKeyboardFactory, SharedKeyboardKeyboardStateMachine, SharedKeyboardKotlinPair<__covariant A, __covariant B>, SharedKeyboardKeyboardLanguageProfile, SharedKeyboardKeyboardLanguageProfiles, SharedKeyboardKeyboardLogicCompanion, SharedKeyboardSuggestionAcceptance, SharedKeyboardPredictionDomain, SharedKeyboardPredictionProfileBundle, SharedKeyboardSingleSwipeBindingCompanion, SharedKeyboardSingleSwipeBindingAction, SharedKeyboardSingleSwipeBindingCharacter, SharedKeyboardWordPredictionEngineCompanion, SharedKeyboardWordPredictionEngine, SharedKeyboardKotlinThrowable, SharedKeyboardKotlinException, SharedKeyboardKotlinRuntimeException, SharedKeyboardKotlinIllegalStateException;

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
@property (class, readonly) SharedKeyboardColorPaletteType *custom __attribute__((swift_name("custom")));
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
- (NSString *)contrastTextColorHex:(NSString *)hex paletteType:(SharedKeyboardColorPaletteType * _Nullable)paletteType __attribute__((swift_name("contrastTextColor(hex:paletteType:)")));
- (NSString *)getColorForDirectionHexDir:(SharedKeyboardDirection *)dir paletteType:(SharedKeyboardColorPaletteType *)paletteType __attribute__((swift_name("getColorForDirectionHex(dir:paletteType:)")));
- (NSString *)getColorForDirectionHex6Dir:(SharedKeyboardDirection *)dir paletteType:(SharedKeyboardColorPaletteType *)paletteType __attribute__((swift_name("getColorForDirectionHex6(dir:paletteType:)")));
- (NSArray<SharedKeyboardColorEntry *> *)getCustomPalette __attribute__((swift_name("getCustomPalette()")));
- (NSArray<SharedKeyboardColorEntry *> *)getCustomPalette6 __attribute__((swift_name("getCustomPalette6()")));
- (NSArray<SharedKeyboardColorEntry *> *)getPaletteType:(SharedKeyboardColorPaletteType *)type __attribute__((swift_name("getPalette(type:)")));
- (NSArray<SharedKeyboardColorEntry *> *)getPalette6Type:(SharedKeyboardColorPaletteType *)type __attribute__((swift_name("getPalette6(type:)")));
- (void)setCustomPaletteColors:(NSArray<SharedKeyboardColorEntry *> *)colors __attribute__((swift_name("setCustomPalette(colors:)")));
- (void)setCustomPalette6Colors:(NSArray<SharedKeyboardColorEntry *> *)colors __attribute__((swift_name("setCustomPalette6(colors:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerButton")))
@interface SharedKeyboardControllerButton : SharedKeyboardKotlinEnum<SharedKeyboardControllerButton *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardControllerButton *a __attribute__((swift_name("a")));
@property (class, readonly) SharedKeyboardControllerButton *b __attribute__((swift_name("b")));
@property (class, readonly) SharedKeyboardControllerButton *x __attribute__((swift_name("x")));
@property (class, readonly) SharedKeyboardControllerButton *y __attribute__((swift_name("y")));
@property (class, readonly) SharedKeyboardControllerButton *leftBumper __attribute__((swift_name("leftBumper")));
@property (class, readonly) SharedKeyboardControllerButton *rightBumper __attribute__((swift_name("rightBumper")));
@property (class, readonly) SharedKeyboardControllerButton *leftTrigger __attribute__((swift_name("leftTrigger")));
@property (class, readonly) SharedKeyboardControllerButton *rightTrigger __attribute__((swift_name("rightTrigger")));
@property (class, readonly) SharedKeyboardControllerButton *start __attribute__((swift_name("start")));
@property (class, readonly) SharedKeyboardControllerButton *select __attribute__((swift_name("select")));
+ (SharedKeyboardKotlinArray<SharedKeyboardControllerButton *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardControllerButton *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerConfusionAnalyzer")))
@interface SharedKeyboardControllerConfusionAnalyzer : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)controllerConfusionAnalyzer __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardControllerConfusionAnalyzer *shared __attribute__((swift_name("shared")));
- (SharedKeyboardControllerConfusionDrillSample *)classifyDrillSampleExpectedDirection:(SharedKeyboardDirection *)expectedDirection snapshot:(SharedKeyboardControllerStickSnapshot *)snapshot deadZone:(float)deadZone dialSectionMode:(SharedKeyboardDialSectionMode *)dialSectionMode __attribute__((swift_name("classifyDrillSample(expectedDirection:snapshot:deadZone:dialSectionMode:)")));
- (NSString *)deadZoneBandDeadZone:(float)deadZone __attribute__((swift_name("deadZoneBand(deadZone:)")));
- (SharedKeyboardControllerPassiveSignal * _Nullable)detectSnapBackReversalPreviousDirection:(SharedKeyboardDirection *)previousDirection lastDirectionBeforeRelease:(SharedKeyboardDirection *)lastDirectionBeforeRelease deadZone:(float)deadZone __attribute__((swift_name("detectSnapBackReversal(previousDirection:lastDirectionBeforeRelease:deadZone:)")));
- (NSArray<SharedKeyboardDirection *> *)directionsForModeDialSectionMode:(SharedKeyboardDialSectionMode *)dialSectionMode __attribute__((swift_name("directionsForMode(dialSectionMode:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerConfusionDrillSample")))
@interface SharedKeyboardControllerConfusionDrillSample : SharedKeyboardBase
- (instancetype)initWithExpectedDirection:(SharedKeyboardDirection *)expectedDirection resolvedDirection:(SharedKeyboardDirection *)resolvedDirection confusionType:(SharedKeyboardControllerConfusionType *)confusionType deadZoneBand:(NSString *)deadZoneBand __attribute__((swift_name("init(expectedDirection:resolvedDirection:confusionType:deadZoneBand:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardControllerConfusionDrillSample *)doCopyExpectedDirection:(SharedKeyboardDirection *)expectedDirection resolvedDirection:(SharedKeyboardDirection *)resolvedDirection confusionType:(SharedKeyboardControllerConfusionType *)confusionType deadZoneBand:(NSString *)deadZoneBand __attribute__((swift_name("doCopy(expectedDirection:resolvedDirection:confusionType:deadZoneBand:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKeyboardControllerConfusionType *confusionType __attribute__((swift_name("confusionType")));
@property (readonly) NSString *deadZoneBand __attribute__((swift_name("deadZoneBand")));
@property (readonly) SharedKeyboardDirection *expectedDirection __attribute__((swift_name("expectedDirection")));
@property (readonly) SharedKeyboardDirection *resolvedDirection __attribute__((swift_name("resolvedDirection")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerConfusionType")))
@interface SharedKeyboardControllerConfusionType : SharedKeyboardKotlinEnum<SharedKeyboardControllerConfusionType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardControllerConfusionType *exactMatch __attribute__((swift_name("exactMatch")));
@property (class, readonly) SharedKeyboardControllerConfusionType *adjacentSlip __attribute__((swift_name("adjacentSlip")));
@property (class, readonly) SharedKeyboardControllerConfusionType *mirrorSlip __attribute__((swift_name("mirrorSlip")));
@property (class, readonly) SharedKeyboardControllerConfusionType *deadZoneJitter __attribute__((swift_name("deadZoneJitter")));
@property (class, readonly) SharedKeyboardControllerConfusionType *otherMismatch __attribute__((swift_name("otherMismatch")));
@property (class, readonly) SharedKeyboardControllerConfusionType *snapBackReversal __attribute__((swift_name("snapBackReversal")));
+ (SharedKeyboardKotlinArray<SharedKeyboardControllerConfusionType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardControllerConfusionType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerInputProcessor")))
@interface SharedKeyboardControllerInputProcessor : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)controllerInputProcessor __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardControllerInputProcessor *shared __attribute__((swift_name("shared")));
- (SharedKeyboardControllerStickSnapshot *)resolveStickX:(float)x y:(float)y deadZone:(float)deadZone invertY:(BOOL)invertY dialSectionMode:(SharedKeyboardDialSectionMode *)dialSectionMode __attribute__((swift_name("resolveStick(x:y:deadZone:invertY:dialSectionMode:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerPassiveSignal")))
@interface SharedKeyboardControllerPassiveSignal : SharedKeyboardBase
- (instancetype)initWithConfusionType:(SharedKeyboardControllerConfusionType *)confusionType deadZoneBand:(NSString *)deadZoneBand __attribute__((swift_name("init(confusionType:deadZoneBand:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardControllerPassiveSignal *)doCopyConfusionType:(SharedKeyboardControllerConfusionType *)confusionType deadZoneBand:(NSString *)deadZoneBand __attribute__((swift_name("doCopy(confusionType:deadZoneBand:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKeyboardControllerConfusionType *confusionType __attribute__((swift_name("confusionType")));
@property (readonly) NSString *deadZoneBand __attribute__((swift_name("deadZoneBand")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerState")))
@interface SharedKeyboardControllerState : SharedKeyboardBase
- (instancetype)initWithIsConnected:(BOOL)isConnected controllerName:(NSString *)controllerName leftStickX:(float)leftStickX leftStickY:(float)leftStickY rightStickX:(float)rightStickX rightStickY:(float)rightStickY __attribute__((swift_name("init(isConnected:controllerName:leftStickX:leftStickY:rightStickX:rightStickY:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardControllerState *)doCopyIsConnected:(BOOL)isConnected controllerName:(NSString *)controllerName leftStickX:(float)leftStickX leftStickY:(float)leftStickY rightStickX:(float)rightStickX rightStickY:(float)rightStickY __attribute__((swift_name("doCopy(isConnected:controllerName:leftStickX:leftStickY:rightStickX:rightStickY:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *controllerName __attribute__((swift_name("controllerName")));
@property (readonly) BOOL isConnected __attribute__((swift_name("isConnected")));
@property (readonly) float leftStickX __attribute__((swift_name("leftStickX")));
@property (readonly) float leftStickY __attribute__((swift_name("leftStickY")));
@property (readonly) float rightStickX __attribute__((swift_name("rightStickX")));
@property (readonly) float rightStickY __attribute__((swift_name("rightStickY")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ControllerStickSnapshot")))
@interface SharedKeyboardControllerStickSnapshot : SharedKeyboardBase
- (instancetype)initWithRawX:(float)rawX rawY:(float)rawY adjustedX:(float)adjustedX adjustedY:(float)adjustedY directionSpaceX:(float)directionSpaceX directionSpaceY:(float)directionSpaceY magnitude:(float)magnitude isActive:(BOOL)isActive direction:(SharedKeyboardDirection *)direction __attribute__((swift_name("init(rawX:rawY:adjustedX:adjustedY:directionSpaceX:directionSpaceY:magnitude:isActive:direction:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardControllerStickSnapshot *)doCopyRawX:(float)rawX rawY:(float)rawY adjustedX:(float)adjustedX adjustedY:(float)adjustedY directionSpaceX:(float)directionSpaceX directionSpaceY:(float)directionSpaceY magnitude:(float)magnitude isActive:(BOOL)isActive direction:(SharedKeyboardDirection *)direction __attribute__((swift_name("doCopy(rawX:rawY:adjustedX:adjustedY:directionSpaceX:directionSpaceY:magnitude:isActive:direction:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) float adjustedX __attribute__((swift_name("adjustedX")));
@property (readonly) float adjustedY __attribute__((swift_name("adjustedY")));
@property (readonly) SharedKeyboardDirection *direction __attribute__((swift_name("direction")));
@property (readonly) float directionSpaceX __attribute__((swift_name("directionSpaceX")));
@property (readonly) float directionSpaceY __attribute__((swift_name("directionSpaceY")));
@property (readonly) BOOL isActive __attribute__((swift_name("isActive")));
@property (readonly) float magnitude __attribute__((swift_name("magnitude")));
@property (readonly) float rawX __attribute__((swift_name("rawX")));
@property (readonly) float rawY __attribute__((swift_name("rawY")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayout")))
@interface SharedKeyboardCustomLayout : SharedKeyboardBase
- (instancetype)initWithId:(NSString *)id name:(NSString *)name normalChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)normalChordMap shiftedChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)shiftedChordMap singleSwipeNormalMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeNormalMap singleSwipeShiftedMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeShiftedMap sectionCount:(int32_t)sectionCount __attribute__((swift_name("init(id:name:normalChordMap:shiftedChordMap:singleSwipeNormalMap:singleSwipeShiftedMap:sectionCount:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardCustomLayout *)doCopyId:(NSString *)id name:(NSString *)name normalChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)normalChordMap shiftedChordMap:(NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *)shiftedChordMap singleSwipeNormalMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeNormalMap singleSwipeShiftedMap:(NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *)singleSwipeShiftedMap sectionCount:(int32_t)sectionCount __attribute__((swift_name("doCopy(id:name:normalChordMap:shiftedChordMap:singleSwipeNormalMap:singleSwipeShiftedMap:sectionCount:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *id __attribute__((swift_name("id")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *normalChordMap __attribute__((swift_name("normalChordMap")));
@property (readonly) int32_t sectionCount __attribute__((swift_name("sectionCount")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, NSArray<NSString *> *> *shiftedChordMap __attribute__((swift_name("shiftedChordMap")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *singleSwipeNormalMap __attribute__((swift_name("singleSwipeNormalMap")));
@property (readonly) NSDictionary<SharedKeyboardDirection *, SharedKeyboardSingleSwipeBinding *> *singleSwipeShiftedMap __attribute__((swift_name("singleSwipeShiftedMap")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("CustomLayoutManager")))
@interface SharedKeyboardCustomLayoutManager : SharedKeyboardBase
- (instancetype)initWithStorage:(id<SharedKeyboardCustomLayoutStorage>)storage __attribute__((swift_name("init(storage:)"))) __attribute__((objc_designated_initializer));
@property (class, readonly, getter=companion) SharedKeyboardCustomLayoutManagerCompanion *companion __attribute__((swift_name("companion")));
- (SharedKeyboardCustomLayout *)createBlankName:(NSString *)name sectionCount:(int32_t)sectionCount __attribute__((swift_name("createBlank(name:sectionCount:)")));
- (void)deleteId:(NSString *)id __attribute__((swift_name("delete(id:)")));
- (SharedKeyboardCustomLayout *)duplicateFromBuiltInSourceLayout:(SharedKeyboardLayoutType *)sourceLayout customName:(NSString *)customName sectionCount:(int32_t)sectionCount __attribute__((swift_name("duplicateFromBuiltIn(sourceLayout:customName:sectionCount:)")));
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
__attribute__((swift_name("DialSectionMode")))
@interface SharedKeyboardDialSectionMode : SharedKeyboardKotlinEnum<SharedKeyboardDialSectionMode *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardDialSectionMode *eightSection __attribute__((swift_name("eightSection")));
@property (class, readonly) SharedKeyboardDialSectionMode *sixSection __attribute__((swift_name("sixSection")));
+ (SharedKeyboardKotlinArray<SharedKeyboardDialSectionMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardDialSectionMode *> *entries __attribute__((swift_name("entries")));
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
__attribute__((swift_name("EmojiCatalogPayload")))
@interface SharedKeyboardEmojiCatalogPayload : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)emojiCatalogPayload __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardEmojiCatalogPayload *shared __attribute__((swift_name("shared")));
- (NSString *)load __attribute__((swift_name("load()")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ErickAppTranslations")))
@interface SharedKeyboardErickAppTranslations : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)erickAppTranslations __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardErickAppTranslations *shared __attribute__((swift_name("shared")));
- (NSString *)textLanguage:(SharedKeyboardKeyboardLanguage *)language english:(NSString *)english __attribute__((swift_name("text(language:english:)")));
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
@property (class, readonly) SharedKeyboardInputAction *deleteWord __attribute__((swift_name("deleteWord")));
@property (class, readonly) SharedKeyboardInputAction *toggleShift __attribute__((swift_name("toggleShift")));
@property (class, readonly) SharedKeyboardInputAction *toggleCaps __attribute__((swift_name("toggleCaps")));
@property (class, readonly) SharedKeyboardInputAction *toggleSymbols __attribute__((swift_name("toggleSymbols")));
@property (class, readonly) SharedKeyboardInputAction *toggleEmoji __attribute__((swift_name("toggleEmoji")));
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

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("InputMode")))
@interface SharedKeyboardInputMode : SharedKeyboardKotlinEnum<SharedKeyboardInputMode *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardInputMode *instant __attribute__((swift_name("instant")));
@property (class, readonly) SharedKeyboardInputMode *confirm __attribute__((swift_name("confirm")));
@property (class, readonly) SharedKeyboardInputMode *assisted __attribute__((swift_name("assisted")));
+ (SharedKeyboardKotlinArray<SharedKeyboardInputMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardInputMode *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((swift_name("KeyboardActionDelegate")))
@protocol SharedKeyboardKeyboardActionDelegate
@required
- (void)commitTextText:(NSString *)text __attribute__((swift_name("commitText(text:)")));
- (NSString *)getCurrentWordPrefix __attribute__((swift_name("getCurrentWordPrefix()")));
- (NSString *)getTextBeforeCursorMaxCharacters:(int32_t)maxCharacters __attribute__((swift_name("getTextBeforeCursor(maxCharacters:)")));
- (NSString *)loadPredictionProfile __attribute__((swift_name("loadPredictionProfile()")));
- (void)onModeChangedMode:(SharedKeyboardKeyboardMode *)mode __attribute__((swift_name("onModeChanged(mode:)")));
- (void)onSuggestionsUpdatedSuggestions:(NSArray<NSString *> *)suggestions __attribute__((swift_name("onSuggestionsUpdated(suggestions:)")));
- (void)savePredictionProfileSerializedProfile:(NSString *)serializedProfile __attribute__((swift_name("savePredictionProfile(serializedProfile:)")));
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
__attribute__((swift_name("KeyboardLanguage")))
@interface SharedKeyboardKeyboardLanguage : SharedKeyboardKotlinEnum<SharedKeyboardKeyboardLanguage *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardKeyboardLanguage *english __attribute__((swift_name("english")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *spanish __attribute__((swift_name("spanish")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *portuguese __attribute__((swift_name("portuguese")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *french __attribute__((swift_name("french")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *german __attribute__((swift_name("german")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *italian __attribute__((swift_name("italian")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *norwegianBokmal __attribute__((swift_name("norwegianBokmal")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *danish __attribute__((swift_name("danish")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *swedish __attribute__((swift_name("swedish")));
@property (class, readonly) SharedKeyboardKeyboardLanguage *finnish __attribute__((swift_name("finnish")));
+ (SharedKeyboardKotlinArray<SharedKeyboardKeyboardLanguage *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardKeyboardLanguage *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardLanguageProfile")))
@interface SharedKeyboardKeyboardLanguageProfile : SharedKeyboardBase
- (instancetype)initWithDisplayName:(NSString *)displayName languageCode:(NSString *)languageCode logical8Extras:(NSArray<NSString *> *)logical8Extras symbols6Extras:(NSArray<NSString *> *)symbols6Extras defaultSuggestions:(NSArray<NSString *> *)defaultSuggestions dictionaryWords:(NSDictionary<NSString *, SharedKeyboardInt *> *)dictionaryWords bigrams:(NSDictionary<NSString *, NSArray<SharedKeyboardKotlinPair<NSString *, SharedKeyboardInt *> *> *> *)bigrams __attribute__((swift_name("init(displayName:languageCode:logical8Extras:symbols6Extras:defaultSuggestions:dictionaryWords:bigrams:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardKeyboardLanguageProfile *)doCopyDisplayName:(NSString *)displayName languageCode:(NSString *)languageCode logical8Extras:(NSArray<NSString *> *)logical8Extras symbols6Extras:(NSArray<NSString *> *)symbols6Extras defaultSuggestions:(NSArray<NSString *> *)defaultSuggestions dictionaryWords:(NSDictionary<NSString *, SharedKeyboardInt *> *)dictionaryWords bigrams:(NSDictionary<NSString *, NSArray<SharedKeyboardKotlinPair<NSString *, SharedKeyboardInt *> *> *> *)bigrams __attribute__((swift_name("doCopy(displayName:languageCode:logical8Extras:symbols6Extras:defaultSuggestions:dictionaryWords:bigrams:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSDictionary<NSString *, NSArray<SharedKeyboardKotlinPair<NSString *, SharedKeyboardInt *> *> *> *bigrams __attribute__((swift_name("bigrams")));
@property (readonly) NSArray<NSString *> *defaultSuggestions __attribute__((swift_name("defaultSuggestions")));
@property (readonly) NSDictionary<NSString *, SharedKeyboardInt *> *dictionaryWords __attribute__((swift_name("dictionaryWords")));
@property (readonly) NSString *displayName __attribute__((swift_name("displayName")));
@property (readonly) NSString *languageCode __attribute__((swift_name("languageCode")));
@property (readonly) NSArray<NSString *> *logical8Extras __attribute__((swift_name("logical8Extras")));
@property (readonly) NSArray<NSString *> *symbols6Extras __attribute__((swift_name("symbols6Extras")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardLanguageProfiles")))
@interface SharedKeyboardKeyboardLanguageProfiles : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)keyboardLanguageProfiles __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardKeyboardLanguageProfiles *shared __attribute__((swift_name("shared")));
- (NSString *)localeTagLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("localeTag(language:)")));
- (NSDictionary<SharedKeyboardDirection *, NSDictionary<SharedKeyboardInt *, NSString *> *> *)logical8NormalOverlayLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("logical8NormalOverlay(language:)")));
- (NSDictionary<SharedKeyboardDirection *, NSDictionary<SharedKeyboardInt *, NSString *> *> *)logical8ShiftedOverlayLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("logical8ShiftedOverlay(language:)")));
- (SharedKeyboardKeyboardLanguageProfile *)profileLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("profile(language:)")));
- (BOOL)supportsEfficiencyLayoutLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("supportsEfficiencyLayout(language:)")));
- (NSDictionary<SharedKeyboardDirection *, NSDictionary<SharedKeyboardInt *, NSString *> *> *)symbols6NormalOverlayLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("symbols6NormalOverlay(language:)")));
- (NSDictionary<SharedKeyboardDirection *, NSDictionary<SharedKeyboardInt *, NSString *> *> *)symbols6ShiftedOverlayLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("symbols6ShiftedOverlay(language:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardLogic")))
@interface SharedKeyboardKeyboardLogic : SharedKeyboardBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@property (class, readonly, getter=companion) SharedKeyboardKeyboardLogicCompanion *companion __attribute__((swift_name("companion")));
- (NSArray<SharedKeyboardKotlinPair<SharedKeyboardDirection *, NSString *> *> *)getCharactersAtPositionRightDir:(SharedKeyboardDirection *)rightDir mode:(SharedKeyboardKeyboardMode *)mode layout:(SharedKeyboardLayoutType *)layout customLayout:(SharedKeyboardCustomLayout * _Nullable)customLayout __attribute__((swift_name("getCharactersAtPosition(rightDir:mode:layout:customLayout:)")));
- (NSArray<NSString *> *)getCharactersForDirectionDir:(SharedKeyboardDirection *)dir mode:(SharedKeyboardKeyboardMode *)mode layout:(SharedKeyboardLayoutType *)layout customLayout:(SharedKeyboardCustomLayout * _Nullable)customLayout __attribute__((swift_name("getCharactersForDirection(dir:mode:layout:customLayout:)")));
- (NSString *)getChordResultLeftDir:(SharedKeyboardDirection *)leftDir rightDir:(SharedKeyboardDirection *)rightDir mode:(SharedKeyboardKeyboardMode *)mode layout:(SharedKeyboardLayoutType *)layout customLayout:(SharedKeyboardCustomLayout * _Nullable)customLayout __attribute__((swift_name("getChordResult(leftDir:rightDir:mode:layout:customLayout:)")));
- (SharedKeyboardDirection *)getDirectionFromXYX:(float)x y:(float)y __attribute__((swift_name("getDirectionFromXY(x:y:)")));
- (NSArray<SharedKeyboardDirection *> *)getDirections __attribute__((swift_name("getDirections()")));
- (NSArray<SharedKeyboardDirection *> *)getPreviewDirections __attribute__((swift_name("getPreviewDirections()")));
- (id _Nullable)getSingleSwipeResultDir:(SharedKeyboardDirection *)dir mode:(SharedKeyboardKeyboardMode *)mode customLayout:(SharedKeyboardCustomLayout * _Nullable)customLayout __attribute__((swift_name("getSingleSwipeResult(dir:mode:customLayout:)")));
@property SharedKeyboardKeyboardLanguage *activeLanguage __attribute__((swift_name("activeLanguage")));
@property SharedKeyboardDialSectionMode *dialSectionMode __attribute__((swift_name("dialSectionMode")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardLogic.Companion")))
@interface SharedKeyboardKeyboardLogicCompanion : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardKeyboardLogicCompanion *shared __attribute__((swift_name("shared")));
@property (readonly) NSArray<SharedKeyboardDirection *> *directions6 __attribute__((swift_name("directions6")));
@property (readonly) NSArray<SharedKeyboardDirection *> *directions8 __attribute__((swift_name("directions8")));
@property (readonly) NSArray<SharedKeyboardDirection *> *previewDirections6 __attribute__((swift_name("previewDirections6")));
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
@property (class, readonly) SharedKeyboardKeyboardMode *symbols __attribute__((swift_name("symbols")));
@property (class, readonly) SharedKeyboardKeyboardMode *symbolsShifted __attribute__((swift_name("symbolsShifted")));
@property (class, readonly) SharedKeyboardKeyboardMode *emoji __attribute__((swift_name("emoji")));
+ (SharedKeyboardKotlinArray<SharedKeyboardKeyboardMode *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardKeyboardMode *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KeyboardStateMachine")))
@interface SharedKeyboardKeyboardStateMachine : SharedKeyboardBase
- (instancetype)initWithDelegate:(id<SharedKeyboardKeyboardActionDelegate>)delegate coroutineScope:(id<SharedKeyboardKotlinx_coroutines_coreCoroutineScope>)coroutineScope __attribute__((swift_name("init(delegate:coroutineScope:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardSuggestionAcceptance *)acceptSuggestionSuggestion:(NSString *)suggestion textBeforeCursor:(NSString *)textBeforeCursor textAfterCursor:(NSString *)textAfterCursor __attribute__((swift_name("acceptSuggestion(suggestion:textBeforeCursor:textAfterCursor:)")));
- (BOOL)areBothDialsAtHome __attribute__((swift_name("areBothDialsAtHome()")));
- (SharedKeyboardKeyboardStateMachine *)createKeyboardStateMachineForIOSDelegate:(id<SharedKeyboardKeyboardActionDelegate>)delegate __attribute__((swift_name("createKeyboardStateMachineForIOS(delegate:)")));
- (NSArray<SharedKeyboardKotlinPair<SharedKeyboardDirection *, NSString *> *> *)getCharactersAtPositionRightDir:(SharedKeyboardDirection *)rightDir __attribute__((swift_name("getCharactersAtPosition(rightDir:)")));
- (NSArray<NSString *> *)getCharactersForDirectionDir:(SharedKeyboardDirection *)dir __attribute__((swift_name("getCharactersForDirection(dir:)")));
- (NSArray<SharedKeyboardColorEntry *> *)getCurrentPalette __attribute__((swift_name("getCurrentPalette()")));
- (NSString *)getCurrentWordBuffer __attribute__((swift_name("getCurrentWordBuffer()")));
- (SharedKeyboardDialSectionMode *)getDialSectionMode __attribute__((swift_name("getDialSectionMode()")));
- (NSArray<SharedKeyboardDirection *> *)getDirections __attribute__((swift_name("getDirections()")));
- (SharedKeyboardKeyboardLanguage *)getKeyboardLanguage __attribute__((swift_name("getKeyboardLanguage()")));
- (NSArray<SharedKeyboardDirection *> *)getPreviewDirections __attribute__((swift_name("getPreviewDirections()")));
- (NSString *)getPreviewText __attribute__((swift_name("getPreviewText()")));
- (void)handleControllerButtonButton:(SharedKeyboardControllerButton *)button __attribute__((swift_name("handleControllerButton(button:)")));
- (void)handleControllerInputLeftX:(float)leftX leftY:(float)leftY rightX:(float)rightX rightY:(float)rightY __attribute__((swift_name("handleControllerInput(leftX:leftY:rightX:rightY:)")));
- (void)handleTouchX:(float)x y:(float)y isLeft:(BOOL)isLeft actionDownOrMove:(BOOL)actionDownOrMove actionUp:(BOOL)actionUp __attribute__((swift_name("handleTouch(x:y:isLeft:actionDownOrMove:actionUp:)")));
- (BOOL)isSymbolsMode __attribute__((swift_name("isSymbolsMode()")));
- (void)refreshAutoCapitalization __attribute__((swift_name("refreshAutoCapitalization()")));
- (void)setAutoCapitalizationEnabledEnabled:(BOOL)enabled __attribute__((swift_name("setAutoCapitalizationEnabled(enabled:)")));
- (void)setColorPalettePalette:(SharedKeyboardColorPaletteType *)palette __attribute__((swift_name("setColorPalette(palette:)")));
- (void)setControllerDeadZoneDeadZone:(float)deadZone __attribute__((swift_name("setControllerDeadZone(deadZone:)")));
- (void)setControllerYAxisInvertedInverted:(BOOL)inverted __attribute__((swift_name("setControllerYAxisInverted(inverted:)")));
- (void)setDialSectionModeMode:(SharedKeyboardDialSectionMode *)mode __attribute__((swift_name("setDialSectionMode(mode:)")));
- (void)setInputModeMode:(SharedKeyboardInputMode *)mode __attribute__((swift_name("setInputMode(mode:)")));
- (void)setKeyboardLanguageLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("setKeyboardLanguage(language:)")));
- (void)setLayoutTypeLayout:(SharedKeyboardLayoutType *)layout __attribute__((swift_name("setLayoutType(layout:)")));
- (void)setLeftHandedModeEnabled:(BOOL)enabled __attribute__((swift_name("setLeftHandedMode(enabled:)")));
- (void)setPredictionDomainDomain:(SharedKeyboardPredictionDomain *)domain __attribute__((swift_name("setPredictionDomain(domain:)")));
- (void)toggleEmojiPanel __attribute__((swift_name("toggleEmojiPanel()")));
@property SharedKeyboardCustomLayout * _Nullable activeCustomLayout __attribute__((swift_name("activeCustomLayout")));
@property (readonly) SharedKeyboardLayoutType *currentLayoutType __attribute__((swift_name("currentLayoutType")));
@property (readonly) SharedKeyboardKeyboardMode *currentMode __attribute__((swift_name("currentMode")));
@property (readonly) SharedKeyboardColorPaletteType *currentPaletteType __attribute__((swift_name("currentPaletteType")));
@property (readonly) NSArray<NSString *> *currentSuggestions __attribute__((swift_name("currentSuggestions")));
@property (readonly) SharedKeyboardInputMode *inputMode __attribute__((swift_name("inputMode")));
@property (readonly) BOOL isNextWordMode __attribute__((swift_name("isNextWordMode")));
@property (readonly) BOOL leftHandedMode __attribute__((swift_name("leftHandedMode")));
@property (readonly) SharedKeyboardDirection *lockedLeftDir __attribute__((swift_name("lockedLeftDir")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("LayoutType")))
@interface SharedKeyboardLayoutType : SharedKeyboardKotlinEnum<SharedKeyboardLayoutType *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardLayoutType *logical __attribute__((swift_name("logical")));
@property (class, readonly) SharedKeyboardLayoutType *efficiency __attribute__((swift_name("efficiency")));
@property (class, readonly) SharedKeyboardLayoutType *custom __attribute__((swift_name("custom")));
+ (SharedKeyboardKotlinArray<SharedKeyboardLayoutType *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardLayoutType *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PredictionDomain")))
@interface SharedKeyboardPredictionDomain : SharedKeyboardKotlinEnum<SharedKeyboardPredictionDomain *>
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKeyboardPredictionDomain *general __attribute__((swift_name("general")));
@property (class, readonly) SharedKeyboardPredictionDomain *conversation __attribute__((swift_name("conversation")));
@property (class, readonly) SharedKeyboardPredictionDomain *productivity __attribute__((swift_name("productivity")));
@property (class, readonly) SharedKeyboardPredictionDomain *accessibility __attribute__((swift_name("accessibility")));
@property (class, readonly) SharedKeyboardPredictionDomain *gaming __attribute__((swift_name("gaming")));
+ (SharedKeyboardKotlinArray<SharedKeyboardPredictionDomain *> *)values __attribute__((swift_name("values()")));
@property (class, readonly) NSArray<SharedKeyboardPredictionDomain *> *entries __attribute__((swift_name("entries")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PredictionProfileBundle")))
@interface SharedKeyboardPredictionProfileBundle : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)predictionProfileBundle __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardPredictionProfileBundle *shared __attribute__((swift_name("shared")));
- (NSDictionary<SharedKeyboardKeyboardLanguage *, NSString *> *)deserializeSerialized:(NSString *)serialized __attribute__((swift_name("deserialize(serialized:)")));
- (NSString *)serializeProfiles:(NSDictionary<SharedKeyboardKeyboardLanguage *, NSString *> *)profiles __attribute__((swift_name("serialize(profiles:)")));
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
__attribute__((swift_name("SuggestionAcceptance")))
@interface SharedKeyboardSuggestionAcceptance : SharedKeyboardBase
- (instancetype)initWithCharsToDelete:(int32_t)charsToDelete leadingText:(NSString *)leadingText suggestion:(NSString *)suggestion trailingText:(NSString *)trailingText __attribute__((swift_name("init(charsToDelete:leadingText:suggestion:trailingText:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardSuggestionAcceptance *)doCopyCharsToDelete:(int32_t)charsToDelete leadingText:(NSString *)leadingText suggestion:(NSString *)suggestion trailingText:(NSString *)trailingText __attribute__((swift_name("doCopy(charsToDelete:leadingText:suggestion:trailingText:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t charsToDelete __attribute__((swift_name("charsToDelete")));
@property (readonly) NSString *leadingText __attribute__((swift_name("leadingText")));
@property (readonly) NSString *suggestion __attribute__((swift_name("suggestion")));
@property (readonly) NSString *trailingText __attribute__((swift_name("trailingText")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("WordPredictionEngine")))
@interface SharedKeyboardWordPredictionEngine : SharedKeyboardBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
@property (class, readonly, getter=companion) SharedKeyboardWordPredictionEngineCompanion *companion __attribute__((swift_name("companion")));
- (void)addUserWordWord:(NSString *)word count:(int32_t)count __attribute__((swift_name("addUserWord(word:count:)")));
- (BOOL)containsWord:(NSString *)word __attribute__((swift_name("contains(word:)")));
- (NSString *)exportLearnedProfile __attribute__((swift_name("exportLearnedProfile()")));
- (NSArray<NSString *> *)getCompletionsPrefix:(NSString *)prefix limit:(int32_t)limit __attribute__((swift_name("getCompletions(prefix:limit:)")));
- (NSArray<NSString *> *)getCorrectionsWord:(NSString *)word limit:(int32_t)limit maxDistance:(int32_t)maxDistance __attribute__((swift_name("getCorrections(word:limit:maxDistance:)")));
- (NSArray<NSString *> *)getDefaultSuggestionsLimit:(int32_t)limit __attribute__((swift_name("getDefaultSuggestions(limit:)")));
- (NSArray<NSString *> *)getNextWordSuggestionsPreviousWord:(NSString *)previousWord limit:(int32_t)limit __attribute__((swift_name("getNextWordSuggestions(previousWord:limit:)")));
- (NSArray<NSString *> *)getSuggestionsCurrentWord:(NSString *)currentWord limit:(int32_t)limit __attribute__((swift_name("getSuggestions(currentWord:limit:)")));
- (void)importLearnedProfileSerializedProfile:(NSString *)serializedProfile __attribute__((swift_name("importLearnedProfile(serializedProfile:)")));
- (void)insertWord:(NSString *)word frequency:(int32_t)frequency __attribute__((swift_name("insert(word:frequency:)")));
- (void)insertBigramWord:(NSString *)word nextWord:(NSString *)nextWord frequency:(int32_t)frequency __attribute__((swift_name("insertBigram(word:nextWord:frequency:)")));
- (void)learnBigramPreviousWord:(NSString *)previousWord nextWord:(NSString *)nextWord count:(int32_t)count __attribute__((swift_name("learnBigram(previousWord:nextWord:count:)")));
- (void)learnWordWord:(NSString *)word count:(int32_t)count userAdded:(BOOL)userAdded __attribute__((swift_name("learnWord(word:count:userAdded:)")));
- (void)setPredictionDomainDomain:(SharedKeyboardPredictionDomain *)domain __attribute__((swift_name("setPredictionDomain(domain:)")));
@end

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("WordPredictionEngine.Companion")))
@interface SharedKeyboardWordPredictionEngineCompanion : SharedKeyboardBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (class, readonly, getter=shared) SharedKeyboardWordPredictionEngineCompanion *shared __attribute__((swift_name("shared")));
- (SharedKeyboardWordPredictionEngine *)createWithDefaultDictionaryLanguage:(SharedKeyboardKeyboardLanguage *)language __attribute__((swift_name("createWithDefaultDictionary(language:)")));
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

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinPair")))
@interface SharedKeyboardKotlinPair<__covariant A, __covariant B> : SharedKeyboardBase
- (instancetype)initWithFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("init(first:second:)"))) __attribute__((objc_designated_initializer));
- (SharedKeyboardKotlinPair<A, B> *)doCopyFirst:(A _Nullable)first second:(B _Nullable)second __attribute__((swift_name("doCopy(first:second:)")));
- (BOOL)equalsOther:(id _Nullable)other __attribute__((swift_name("equals(other:)")));
- (int32_t)hashCode __attribute__((swift_name("hashCode()")));
- (NSString *)toString __attribute__((swift_name("toString()")));
@property (readonly) A _Nullable first __attribute__((swift_name("first")));
@property (readonly) B _Nullable second __attribute__((swift_name("second")));
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
