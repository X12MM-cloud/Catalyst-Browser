cmake_minimum_required(VERSION 3.10)
project(ZetaBrowserCEF)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find CEF package
find_package(CEF REQUIRED)

# Include directories
include_directories(${CEF_INCLUDE_DIRS})

# Add source files
set(SOURCES
    cef_wrapper.cpp
    cef_client.cpp
    jni_bridge.cpp
)

# Create shared library
add_library(zetabrowser_cef SHARED ${SOURCES})

# Link against CEF libraries
target_link_libraries(zetabrowser_cef PRIVATE ${CEF_LIBRARIES})

# Set output directory
set_target_properties(zetabrowser_cef PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
)

# Copy CEF resources and DLLs
add_custom_command(TARGET zetabrowser_cef POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    ${CEF_BINARY_DIR}/Resources $<TARGET_FILE_DIR:zetabrowser_cef>
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    ${CEF_BINARY_DIR}/Release $<TARGET_FILE_DIR:zetabrowser_cef>
)